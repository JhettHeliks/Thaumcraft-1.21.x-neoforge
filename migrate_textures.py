import os
import json

base_dir = r"C:\Users\jmaki\Documents\minecraft mods folder\Thaumcraft Reloaded\src\main\resources\assets\thaumcraft"
blockstates_dir = os.path.join(base_dir, "blockstates")
models_block_dir = os.path.join(base_dir, "models", "block")
models_item_dir = os.path.join(base_dir, "models", "item")

def fix_texture_paths(obj):
    if isinstance(obj, str):
        obj = obj.replace("thaumcraft:blocks/", "thaumcraft:block/")
        obj = obj.replace("thaumcraft:items/", "thaumcraft:item/")
        return obj
    elif isinstance(obj, dict):
        return {k: fix_texture_paths(v) for k, v in obj.items()}
    elif isinstance(obj, list):
        return [fix_texture_paths(v) for v in obj]
    return obj

# 1 & 2. Migrate Blockstates
for filename in os.listdir(blockstates_dir):
    if not filename.endswith(".json"): continue
    
    filepath = os.path.join(blockstates_dir, filename)
    with open(filepath, 'r', encoding='utf-8') as f:
        data = json.load(f)

    changed = False
    
    # Global string re-alignment mapping away from plural paths
    new_data = fix_texture_paths(data)
    if json.dumps(new_data) != json.dumps(data):
        changed = True
        data = new_data

    # Variant Texture Extractor
    if "variants" in data:
        # Some variants are lists, some are dicts
        for var_name, variant in data["variants"].items():
            if isinstance(variant, dict) and "textures" in variant:
                block_id = filename.replace(".json", "")
                gen_model_name = block_id if var_name == "" else f"{block_id}_{var_name.replace('=', '_')}"
                
                new_model = {
                    "parent": variant.get("model", "block/cube_all"),
                    "textures": variant["textures"]
                }
                
                # Write model
                os.makedirs(models_block_dir, exist_ok=True)
                with open(os.path.join(models_block_dir, f"{gen_model_name}.json"), 'w', encoding='utf-8') as mf:
                    json.dump(new_model, mf, indent=4)
                
                # Strip and patch variant internally
                del variant["textures"]
                variant["model"] = f"thaumcraft:block/{gen_model_name}"
                changed = True
                
            elif isinstance(variant, list):
                for i, subvar in enumerate(variant):
                    if "textures" in subvar:
                        block_id = filename.replace(".json", "")
                        gen_model_name = f"{block_id}_{i}"
                        
                        new_model = {
                            "parent": subvar.get("model", "block/cube_all"),
                            "textures": subvar["textures"]
                        }
                        os.makedirs(models_block_dir, exist_ok=True)
                        with open(os.path.join(models_block_dir, f"{gen_model_name}.json"), 'w', encoding='utf-8') as mf:
                            json.dump(new_model, mf, indent=4)
                        
                        del subvar["textures"]
                        subvar["model"] = f"thaumcraft:block/{gen_model_name}"
                        changed = True

    if changed:
        with open(filepath, 'w', encoding='utf-8') as f:
            json.dump(data, f, indent=4)

# 3. Fix Crystal Names in models/item/
crystal_mapping = {
    "crystal_aer.json": "air_crystal.json",
    "crystal_ignis.json": "fire_crystal.json",
    "crystal_aqua.json": "water_crystal.json",
    "crystal_terra.json": "earth_crystal.json",
    "crystal_ordo.json": "order_crystal.json",
    "crystal_perditio.json": "entropy_crystal.json"
}

for old, new in crystal_mapping.items():
    old_p = os.path.join(models_item_dir, old)
    new_p = os.path.join(models_item_dir, new)
    if os.path.exists(old_p):
        os.rename(old_p, new_p)

# 4. Generate Missing BlockItem Models
required_blockitems = [
    "amber_block", "amber_brick", "arcane_stone", "ancient_stone",
    "greatwood_log", "greatwood_planks", "silverwood_log", "silverwood_planks"
]

for bi in required_blockitems:
    file_p = os.path.join(models_item_dir, f"{bi}.json")
    if not os.path.exists(file_p):
        with open(file_p, 'w', encoding='utf-8') as f:
            json.dump({"parent": f"thaumcraft:block/{bi}"}, f, indent=4)

print("Migration and modeling complete.")
