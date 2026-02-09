# TinyRendererJava

A **CPU-based 3D software renderer written from scratch in Java**, inspired by the *TinyRenderer* project.
This project focuses on understanding the **fundamentals of graphics programming** without relying on GPU APIs like OpenGL or Vulkan.

The renderer is entirely **software-based** and runs on the CPU.

---

## Features

* Software rasterization pipeline implemented from scratch
* Loads and renders **3D object models**
* Supports **smooth shading (Phong shading)**
* Basic transformation pipeline:

  * Model
  * View
  * Projection
* Simple lighting calculations
* Frame-by-frame image rendering
* Generates animations by stitching frames using **ffmpeg**

> ⚠️ Note:
> This renderer currently **does not support texture mapping**. The focus so far has been on geometry processing, transformations, and shading.

---

## Performance

* Resolution: **1000 × 1000**
* Frames rendered: **500**
* Total render time: **~32.67 seconds**
* Average per frame: **~0.062 seconds**
* CPU: **12th Gen Intel i5**
* Rendering is **100% CPU-based**

Rendered frames can be converted into a video using `ffmpeg`.

---

## Project Structure

```
TinyRendererJava/
├── models/          # 3D object models
├── rendered/        # Output rendered frames
├── src/             # Renderer source code
├── build.bat        # Windows build script
├── build.sh         # Linux build script
├── clean.bat        # Cleanup script
├── generate_video.bat # Uses ffmpeg to generate video
└── README.md
```

---

## Build & Run

### Requirements

* Java (JDK 8 or higher)
* ffmpeg (for video generation)

### Build

On Windows:

```bat
build.bat
```

On Linux:

```sh
./build.sh
```

### Run

After building, execute the generated program to render frames.
Rendered images will be saved in the `rendered/` directory.

### Generate Video

```bat
generate_video.bat
```

This uses **ffmpeg** to combine rendered frames into a video.

---

## Planned Improvements

* Texture mapping
* Z-buffer optimizations
* Ambient occlusion
* Bloom
* Physically Based Rendering (PBR)
* Rewriting the renderer in **C** with custom memory arenas

---

## Inspiration

* *TinyRenderer* – an excellent resource for learning graphics programming from first principles.

---

## Disclaimer

This is a **learning project** and the renderer is **not feature-complete**.
The code prioritizes clarity and understanding over optimization and architecture.
