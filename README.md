README of akka-video-modifier

# Python scripts
## audio_extraction.py
```
usage: audio_extraction.py [-h] [-p PATH] [-x EXPORT]

Python script to extract audio from a video

options:
  -h, --help            show this help message and exit
  -p PATH, --path PATH  Target video path. Default is current directory
  -x EXPORT, --export EXPORT
                        Export folder. Default is current directory
```

## subtitles.py

```
usage: subtitles.py [-h] [-p IMAGE_PATH] [-s SUBTITLES] [-x EXPORT_FOLDER]

Python script to add subtitles to an image

options:
  -h, --help            show this help message and exit
  -p IMAGE_PATH, --image-path IMAGE_PATH
                        Target image file
  -s SUBTITLES, --subtitles SUBTITLES
                        Subtitles added to image
  -x EXPORT_FOLDER, --export-folder EXPORT_FOLDER
                        Folder where the edited image will be exported, default is current directory
```

## video_export.py

```
usage: video_export.py [-h] [-f IMAGES_FOLDER] [-a AUDIO_FILE] [-x EXPORT_FOLDER] [-s START] [-e END] [-v VOLUME_FACTOR]

Python script to add audio to video clip

options:
  -h, --help            show this help message and exit
  -f IMAGES_FOLDER, --images-folder IMAGES_FOLDER
                        Target images folder to create video with
  -a AUDIO_FILE, --audio-file AUDIO_FILE
                        Target audio file to embed with the video
  -x EXPORT_FOLDER, --export-folder EXPORT_FOLDER
                        Folder where the vdeo will be exported, default is current directory
  -s START, --start START
                        Start duration of the audio file, default is 0
  -e END, --end END     The end duration of the audio file, default is the length of the video file
  -v VOLUME_FACTOR, --volume-factor VOLUME_FACTOR
                        The volume factor to multiply by the volume of the audio file, 1 means no change, below 1 will decrease volume, above will increase.
```

## video_images_extraction.py

```
usage: video_images_extraction.py [-h] [-v VIDEO_PATH] [-x EXPORT_FOLDER]

Python script to export each frame of a given video

options:
  -h, --help            show this help message and exit
  -v VIDEO_PATH, --video-path VIDEO_PATH
                        Target video file
  -x EXPORT_FOLDER, --export-folder EXPORT_FOLDER
                        Folder where the frames of the vdeo will be exported, default is current directory
```

## cartoon.py
```
usage: cartoon.py [-h] [-p IMAGE_PATH] [-x EXPORT_FOLDER]

Python script to add cartoon filter to image

options:
  -h, --help            show this help message and exit
  -p IMAGE_PATH, --image-path IMAGE_PATH
                        Target image file
  -x EXPORT_FOLDER, --export-folder EXPORT_FOLDER
                        Folder where the edited image will be exported, default is current directory
```