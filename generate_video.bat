@echo off

pushd rendered
ffmpeg -y -framerate 30 -i output-%%03d.png output.mp4
popd