import os
import json
import re

SOURCE_ROOT = r"C:\Users\jmaki\Documents\minecraft mods folder\Thaumcraft-6-Source-Code-master\src\main\resources\assets\thaumcraft"
DEST_ROOT = r"C:\Users\jmaki\Documents\minecraft mods folder\Thaumcraft Reloaded\src\main\resources\assets\thaumcraft"

FLATTENING_KEYS = ["variant", "type", "color", "metal"]

def process_model(model_data, file_path):
    if isinstance(model_data, dict):
        new_data = {}
        for k, v in model_data.items():
            if isinstance(v, (dict, list)):
                new_data[k] = process_model(v, file_path)
            elif isinstance(v, str):
                # Process texture paths and model references
                match = re.search(r'^([a-z0-9_.-]+):(blocks|items)/(.*)$', v)
                if match:
                    domain, folder, path = match.groups()
                    if domain not in ["minecraft", "thaumcraft"]:
                        print(f"FLAG [Unrecognized Domain in {file_path}]: {v}")
                        new_data[k] = v
                    else:
                        new_folder = "block" if folder == "blocks" else "item"
                        new_data[k] = f"{domain}:{new_folder}/{path}"
                else:
                    new_data[k] = v
            else:
                new_data[k] = v
        return new_data
    elif isinstance(model_data, list):
        return [process_model(item, file_path) for item in model_data]
    else:
        return model_data

def migrate_models():
    src_dir = os.path.join(SOURCE_ROOT, "models", "block")
    dest_dir = os.path.join(DEST_ROOT, "models", "block")
    os.makedirs(dest_dir, exist_ok=True)
    
    if not os.path.exists(src_dir):
         print(f"Source models dir not found: {src_dir}")
         return

    for root, _, files in os.walk(src_dir):
        for file in files:
            if file.endswith(".json"):
                src_path = os.path.join(root, file)
                rel_path = os.path.relpath(src_path, src_dir)
                dest_path = os.path.join(dest_dir, rel_path)
                os.makedirs(os.path.dirname(dest_path), exist_ok=True)
                
                with open(src_path, "r") as f:
                    try:
                        data = json.load(f)
                    except Exception as e:
                        print(f"FLAG [JSON Decode Error]: {src_path}")
                        continue
                        
                new_data = process_model(data, src_path)
                
                with open(dest_path, "w") as f:
                    json.dump(new_data, f, indent=4)
                    
def migrate_blockstates():
    src_dir = os.path.join(SOURCE_ROOT, "blockstates")
    dest_dir = os.path.join(DEST_ROOT, "blockstates")
    os.makedirs(dest_dir, exist_ok=True)
    
    if not os.path.exists(src_dir):
         print(f"Source blockstates dir not found: {src_dir}")
         return

    for root, _, files in os.walk(src_dir):
        for file in files:
            if file.endswith(".json"):
                src_path = os.path.join(root, file)
                basename = file[:-5]
                
                with open(src_path, "r") as f:
                    try:
                        data = json.load(f)
                    except Exception as e:
                        print(f"FLAG [JSON Decode Error]: {src_path}")
                        continue
                
                defaults = data.get("defaults", {})
                if "forge_marker" in data:
                    data.pop("forge_marker")
                if "defaults" in data:
                    data.pop("defaults")
                
                # Flatten the variants
                flattened_states = {} # map of new_filename -> blockstate json
                
                if "variants" in data:
                    for var_keys, var_vals in data["variants"].items():
                        if var_keys == "inventory":
                            continue 
                            
                        # var_vals can be dict or list of dicts
                        if isinstance(var_vals, dict):
                            var_vals = [var_vals]
                        elif not isinstance(var_vals, list):
                            continue
                            
                        # apply defaults to each variant option
                        new_var_vals = []
                        for opt in var_vals:
                            merged = process_model(defaults.copy(), src_path)
                            merged.update(process_model(opt, src_path))
                            
                            # fix model paths - if it doesn't have a prefix, assume thaumcraft:block/
                            if "model" in merged:
                                m = merged["model"]
                                if ":" not in m:
                                    if "/" not in m:
                                        merged["model"] = f"minecraft:block/{m}"
                                    else:
                                        merged["model"] = m
                                else:
                                    domain, path = m.split(":", 1)
                                    if "/" not in path:
                                        merged["model"] = f"{domain}:block/{path}"
                            
                            if "transform" in merged:
                                del merged["transform"]
                                
                            new_var_vals.append(merged)
                            
                        if var_keys == "normal":
                            split_keys = {}
                        else:
                            # Safely split avoiding bad format
                            try:
                                split_keys = dict(kv.split("=") for kv in var_keys.split(",") if "=" in kv)
                            except Exception:
                                split_keys = {"raw": var_keys}
                            
                        flatten_prefix = ""
                        for fk in FLATTENING_KEYS:
                            if fk in split_keys:
                                flatten_prefix = split_keys.pop(fk)
                                break
                                
                        new_basename = f"{flatten_prefix}_{basename}" if flatten_prefix else basename
                        
                        if split_keys:
                            new_var_key = ",".join(f"{k}={v}" for k,v in split_keys.items())
                        else:
                            new_var_key = ""
                            
                        if new_basename not in flattened_states:
                            flattened_states[new_basename] = {"variants": {}}
                            
                        if len(new_var_vals) == 1:
                            new_var_vals = new_var_vals[0]
                            
                        flattened_states[new_basename]["variants"][new_var_key] = new_var_vals
                elif "multipart" in data:
                    # just apply defaults if any to each part, don't flatten file
                    new_multipart = []
                    for part in data["multipart"]:
                        apply = part.get("apply", {})
                        if isinstance(apply, dict):
                            apply_list = [apply]
                        else:
                            apply_list = apply
                            
                        new_apply_list = []
                        for opt in apply_list:
                            merged = process_model(defaults.copy(), src_path)
                            merged.update(process_model(opt, src_path))
                            if "model" in merged:
                                m = merged["model"]
                                if ":" not in m:
                                    if "/" not in m:
                                        merged["model"] = f"minecraft:block/{m}"
                                else:
                                    domain, path = m.split(":", 1)
                                    if "/" not in path:
                                        merged["model"] = f"{domain}:block/{path}"
                            if "transform" in merged:
                                del merged["transform"]
                            new_apply_list.append(merged)
                            
                        if len(new_apply_list) == 1:
                            new_apply_list = new_apply_list[0]
                            
                        part["apply"] = new_apply_list
                        new_multipart.append(part)
                    data["multipart"] = new_multipart
                    flattened_states[basename] = data

                for prefix_bn, state_json in flattened_states.items():
                    dest_path = os.path.join(dest_dir, f"{prefix_bn}.json")
                    with open(dest_path, "w") as f:
                        json.dump(state_json, f, indent=4)

if __name__ == "__main__":
    print("Migrating Models...")
    migrate_models()
    print("Migrating Blockstates...")
    migrate_blockstates()
    print("Migration Complete.")
