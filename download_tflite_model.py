#!/usr/bin/env python3
"""
TFLite Model Downloader for Semantic Search

Downloads the paraphrase-multilingual-MiniLM-L12-v2 model in TFLite format
and places it in the app's assets directory.
"""

import os
import urllib.request
import shutil
from pathlib import Path

def download_file(url, destination):
    """Download a file with progress"""
    print(f"Downloading from {url}...")
    print(f"Destination: {destination}")
    
    try:
        # Create directory if it doesn't exist
        os.makedirs(os.path.dirname(destination), exist_ok=True)
        
        # Download with progress
        def reporthook(blocknum, blocksize, totalsize):
            percent = min(100, blocknum * blocksize * 100 / totalsize)
            print(f"\rProgress: {percent:.1f}%", end='', flush=True)
        
        urllib.request.urlretrieve(url, destination, reporthook)
        print("\n✓ Download complete!")
        return True
        
    except Exception as e:
        print(f"\n✗ Download failed: {e}")
        return False

def main():
    print("=" * 60)
    print("TFLite Model Downloader for HelloGerman Dictionary")
    print("=" * 60)
    print()
    
    # Target directory
    models_dir = Path("app/src/main/assets/models")
    models_dir.mkdir(parents=True, exist_ok=True)
    
    # Model file path
    model_path = models_dir / "multilingual_embeddings.tflite"
    
    print("📦 Model Information:")
    print("  Name: paraphrase-multilingual-MiniLM-L12-v2")
    print("  Type: TensorFlow Lite")
    print("  Size: ~80 MB")
    print("  Dimensions: 384")
    print("  Languages: 50+ (including German & English)")
    print()
    
    # Check if already exists
    if model_path.exists():
        print(f"⚠️  Model already exists at: {model_path}")
        response = input("Do you want to re-download? (y/N): ")
        if response.lower() != 'y':
            print("Skipping download.")
            return
    
    print("🔍 Looking for pre-converted TFLite model...")
    print()
    
    # Option 1: Direct TFLite download (if available)
    # Note: TFLite models are often not directly available on Hugging Face
    # You may need to convert manually
    
    tflite_urls = [
        # Try common repositories for pre-converted models
        "https://storage.googleapis.com/tfhub-lite-models/sentence-transformers/paraphrase-multilingual-MiniLM-L12-v2/1.tflite",
        "https://github.com/UKPLab/sentence-transformers/releases/download/v2.0.0/paraphrase-multilingual-MiniLM-L12-v2.tflite",
    ]
    
    print("Attempting to download pre-converted TFLite model...")
    print()
    
    for url in tflite_urls:
        print(f"Trying: {url}")
        success = download_file(url, str(model_path))
        if success:
            print()
            print("✅ SUCCESS! Model downloaded and placed at:")
            print(f"   {model_path}")
            print()
            print("You can now build and run the app with semantic search enabled!")
            return
    
    print()
    print("❌ Pre-converted TFLite model not found at common URLs.")
    print()
    print("=" * 60)
    print("MANUAL CONVERSION REQUIRED")
    print("=" * 60)
    print()
    print("To use semantic search, you need to convert the model manually:")
    print()
    print("Option 1: Use TensorFlow Model Converter")
    print("-" * 60)
    print("1. Install TensorFlow:")
    print("   pip install tensorflow tensorflow-hub sentence-transformers")
    print()
    print("2. Run this Python script:")
    print("""
import tensorflow as tf
from sentence_transformers import SentenceTransformer

# Load the model
model = SentenceTransformer('paraphrase-multilingual-MiniLM-L12-v2')

# Convert to TFLite
converter = tf.lite.TFLiteConverter.from_saved_model('path/to/saved/model')
converter.optimizations = [tf.lite.Optimize.DEFAULT]
tflite_model = converter.convert()

# Save
with open('app/src/main/assets/models/multilingual_embeddings.tflite', 'wb') as f:
    f.write(tflite_model)
""")
    print()
    print("Option 2: Use a Pre-quantized Alternative")
    print("-" * 60)
    print("Use a smaller, quantized model that's easier to convert:")
    print("- all-MiniLM-L6-v2 (80MB → 40MB)")
    print("- distiluse-base-multilingual-cased-v2 (220MB)")
    print()
    print("Option 3: Skip Semantic Search")
    print("-" * 60)
    print("The app works without the model!")
    print("- Exact search works perfectly")
    print("- Advanced gender detection works")
    print("- Enhanced examples work")
    print("- Only semantic search/synonyms are disabled")
    print()
    print("=" * 60)

if __name__ == '__main__':
    main()

