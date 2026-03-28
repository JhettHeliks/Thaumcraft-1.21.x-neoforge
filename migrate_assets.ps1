$ErrorActionPreference = 'Stop'

$SOURCE_ROOT = "C:\Users\jmaki\Documents\minecraft mods folder\Thaumcraft-6-Source-Code-master\src\main\resources\assets\thaumcraft"
$DEST_ROOT = "C:\Users\jmaki\Documents\minecraft mods folder\Thaumcraft Reloaded\src\main\resources\assets\thaumcraft"
$FLATTENING_KEYS = @("variant", "type", "color", "metal")

function Process-Model {
    param ($ModelData, $FilePath)
    
    if ($ModelData -is [System.Management.Automation.PSCustomObject] -or $ModelData -is [hashtable]) {
        $newData = [ordered]@{}
        foreach ($prop in $ModelData.psobject.properties) {
            $k = $prop.Name
            $v = $prop.Value
            
            if ($v -is [System.Management.Automation.PSCustomObject] -or $v -is [hashtable] -or $v -is [array]) {
                $newData[$k] = Process-Model -ModelData $v -FilePath $FilePath
            } elseif ($v -is [string]) {
                if ($v -match "^([a-z0-9_.-]+):(blocks|items)/(.*)$") {
                    $domain = $matches[1]
                    $folder = $matches[2]
                    $path = $matches[3]
                    
                    if ($domain -notin @("minecraft", "thaumcraft")) {
                        Write-Host "FLAG [Unrecognized Domain in $FilePath]: $v"
                        $newData[$k] = $v
                    } else {
                        $newFolder = if ($folder -eq "blocks") { "block" } else { "item" }
                        $newData[$k] = "${domain}:${newFolder}/${path}"
                    }
                } else {
                    $newData[$k] = $v
                }
            } else {
                $newData[$k] = $v
            }
        }
        return $newData
    } elseif ($ModelData -is [array]) {
        $newList = [System.Collections.ArrayList]::new()
        foreach ($item in $ModelData) {
            [void]$newList.Add((Process-Model -ModelData $item -FilePath $FilePath))
        }
        return $newList.ToArray()
    } else {
        return $ModelData
    }
}

function Migrate-Models {
    $srcDir = Join-Path $SOURCE_ROOT "models\block"
    $destDir = Join-Path $DEST_ROOT "models\block"
    if (-not (Test-Path $srcDir)) { Write-Host "Source models dir not found"; return }
    $files = Get-ChildItem -Path $srcDir -Filter "*.json" -Recurse
    foreach ($file in $files) {
        $relPath = $file.FullName.Substring($srcDir.Length + 1)
        $destPath = Join-Path $destDir $relPath
        $destParent = Split-Path $destPath
        if (-not (Test-Path $destParent)) { New-Item -ItemType Directory -Path $destParent -Force | Out-Null }
        
        try {
            $data = Get-Content $file.FullName -Raw | ConvertFrom-Json
            $newData = Process-Model -ModelData $data -FilePath $file.FullName
            $newData | ConvertTo-Json -Depth 100 | Set-Content $destPath
        } catch {
            Write-Host "FLAG [JSON Decode Error]: $($file.FullName)"
        }
    }
}

function Migrate-Blockstates {
    $srcDir = Join-Path $SOURCE_ROOT "blockstates"
    $destDir = Join-Path $DEST_ROOT "blockstates"
    if (-not (Test-Path $destDir)) { New-Item -ItemType Directory -Path $destDir -Force | Out-Null }
    if (-not (Test-Path $srcDir)) { Write-Host "Source blockstates dir not found"; return }
    $files = Get-ChildItem -Path $srcDir -Filter "*.json" -Recurse
    
    foreach ($file in $files) {
        $basename = $file.BaseName
        try {
            $data = Get-Content $file.FullName -Raw | ConvertFrom-Json
        } catch {
            Write-Host "FLAG [JSON Decode Error]: $($file.FullName)"
            continue
        }
        
        $defaults = @{}
        if ($data.psobject.properties.Match("defaults").Count -gt 0) {
            $defaults = $data.defaults
            $data.psobject.properties.Remove("defaults")
        }
        if ($data.psobject.properties.Match("forge_marker").Count -gt 0) {
            $data.psobject.properties.Remove("forge_marker")
        }
        
        $flattenedStates = @{}
        
        if ($data.psobject.properties.Match('variants').Count -gt 0) {
            $variants = $data.variants
            foreach ($varProp in $variants.psobject.properties) {
                $varKeys = $varProp.Name
                $varValsOriginal = $varProp.Value
                if ($varKeys -eq "inventory") { continue }
                
                $varVals = [System.Collections.ArrayList]::new()
                if ($varValsOriginal -is [array]) {
                    $varVals.AddRange($varValsOriginal)
                } else {
                    [void]$varVals.Add($varValsOriginal)
                }
                
                $newVarVals = [System.Collections.ArrayList]::new()
                foreach ($opt in $varVals) {
                    $merged = [ordered]@{}
                    # apply defaults
                    if ($defaults -is [System.Management.Automation.PSCustomObject] -or $defaults -is [hashtable]) {
                        foreach ($dp in $defaults.psobject.properties) {
                            $merged[$dp.Name] = $dp.Value
                        }
                    }
                    if ($opt -is [System.Management.Automation.PSCustomObject] -or $opt -is [hashtable]) {
                        foreach ($op in $opt.psobject.properties) {
                            $merged[$op.Name] = $op.Value
                        }
                    }
                    
                    $merged = Process-Model -ModelData $merged -FilePath $file.FullName
                    
                    if ($merged.Contains("model")) {
                        $m = $merged["model"]
                        if ($m -notmatch ":") {
                            if ($m -notmatch "/") {
                                $merged["model"] = "minecraft:block/$m"
                            }
                        } else {
                            $parts = $m -split ":"
                            $domain = $parts[0]
                            $path = $parts[1]
                            if ($path -notmatch "/") {
                                $merged["model"] = "${domain}:block/${path}"
                            }
                        }
                    }
                    if ($merged.Contains("transform")) {
                        $merged.Remove("transform")
                    }
                    [void]$newVarVals.Add($merged)
                }
                
                $splitKeys = [ordered]@{}
                if ($varKeys -eq "normal") {
                    # leave empty
                } else {
                    foreach ($kv in $varKeys -split ",") {
                        if ($kv -match "=") {
                            $parts = $kv -split "=", 2
                            $splitKeys[$parts[0]] = $parts[1]
                        }
                    }
                }
                
                $flattenPrefix = ""
                foreach ($fk in $FLATTENING_KEYS) {
                    if ($splitKeys.Contains($fk)) {
                        $flattenPrefix = $splitKeys[$fk]
                        $splitKeys.Remove($fk)
                        break
                    }
                }
                
                $newBasename = if ($flattenPrefix) { "${flattenPrefix}_${basename}" } else { $basename }
                $newVarKeyList = [System.Collections.ArrayList]::new()
                foreach ($k in $splitKeys.Keys) {
                    [void]$newVarKeyList.Add("$k=$($splitKeys[$k])")
                }
                $newVarKeyStr = $newVarKeyList -join ","
                
                if (-not $flattenedStates.ContainsKey($newBasename)) {
                    $fs = [ordered]@{ "variants" = [ordered]@{} }
                    $flattenedStates[$newBasename] = $fs
                }
                
                if ($newVarVals.Count -eq 1) {
                    $flattenedStates[$newBasename]["variants"]["$newVarKeyStr"] = $newVarVals[0]
                } else {
                    $flattenedStates[$newBasename]["variants"]["$newVarKeyStr"] = $newVarVals.ToArray()
                }
            }
        } elseif ($data.psobject.properties.Match("multipart").Count -gt 0) {
            $flattenedStates[$basename] = $data
        }
        
        foreach ($prefixBn in $flattenedStates.Keys) {
            $destPath = Join-Path $destDir "$prefixBn.json"
            $flattenedStates[$prefixBn] | ConvertTo-Json -Depth 100 | Set-Content $destPath
        }
    }
}

Write-Host "Migrating Models..."
Migrate-Models
Write-Host "Migrating Blockstates..."
Migrate-Blockstates
Write-Host "Migration Complete."
