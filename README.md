README of akka-video-modifier

# <font color="blue"> What is it ?</font>

akka-video-modifier is a java program that use the Akka library to modify some videos. 
The modifications available are currently to add subtitles and add a cartoon filter. 
The modifications are done with Python scripts.

The program has two parts : one is the master part, that distributes tasks, the other is the worker that handle 
those tasks and send back the results. Here a task is a modification on a unique frame of a video. 

A worker can be launched on the same machine as the master, or on another one. 
Multiple workers can be launched on multiple machines, all connected to the master.
This makes it possible to parallelize tasks and increase the execution speed based on the number of machines.

For example, we tested this on a 12 raspberry pi cluster (~420 €) to compare with a 600€ computer.

tests : add subtitles to a 10 seconds video and removes all colors except yellow
```
PC with 4 workers : 74 seconds
PC with 8 workers : 61 seconds
PC with 48 workers : 52 seconds
Cluster 12 raspberry pi 4 : 48 workers + 4 workers on PC : 40 seconds
Cluster 12 raspberry pi 4 : 96 workers + 4 workers on PC : 40 seconds

PC CPU specs : Intel® Core™ i5-9300H CPU @ 2.40GHz × 8 
Pi specs : Raspberry Pi 4 Model B Rev 1.1
```

# <font color="blue"> How to use it</font>

If you want to use the program, you can either launch it with a master alone, but you have to launch it with workers, or use a master -with or without workers-  and launch the workers on another computer(s).

Each part has some parameters to use it.

## <font color="blue"> Master </font>

```
master      Start a master ActorSystem.
  Usage: master [options]
    Options:
      -c, --cartoon
        Enable the videos' modification into a cartoon
        Default: false
      -h, --host
        This machine's host name or IP that we use to bind this 
        application against
        Default: 10.137.35.187
      -ip, --inputPath
        Input path for the input data; all files in this folder are 
        considered 
        Default: data/videos
      -w, --numWorkers
        The number of workers (indexers/validators) to start locally; 
        should be at least one if the algorithm is started standalone 
        (otherwise there are no workers to run the discovery)
        Default: 4
      -p, --port
        This machines port that we use to bind this application against
        Default: 7877
      -pc, --pythoncommand
        Python command used to launch python script
        Default: python3
```

### Example Linux command for raspberries cluster's master

```bash
java -jar jar/akka-video-modifier.jar master -w 0 -h 10.42.0.1 -c
```


## <font color="blue"> Worker </font>

```
worker      Start a worker ActorSystem.
  Usage: worker [options]
    Options:
      -h, --host
        This machine's host name or IP that we use to bind this 
        application against
        Default: 10.137.35.187
      -mh, --masterhost
        The host name or IP of the master
        Default: 10.137.35.187
      -mp, --masterport
        The port of the master
        Default: 7877
      -w, --numWorkers
        The number of workers (indexers/validators) to start locally; 
        should be at least one if the algorithm is started standalone 
        (otherwise there are no workers to run the discovery)
        Default: 4
      -p, --port
        This machines port that we use to bind this application against
        Default: 7879
      -pc, --pythoncommand
        Python command used to launch python script
        Default: python3
```

### Example Linux command for raspberries cluster

We were using a raspberry cluster where raspberry hostname was piXX (with XX a number between 01 and 12) so we used this to set up ports and host address.
The master IP was 10.42.0.1
```bash
java -jar jar/akka-video-modifier.jar worker -w 4 -mh 10.42.0.1 -p $(expr 7879 + $(hostname | grep -o '[0-9]\+')) -h $(hostname).local -pc python3
```

# <font color="blue"> Python scripts</font>

The program use the Python scripts to modify the videos, but each script can be use in standalone. Here a description for each script :

## <font color="blue"> audio_extraction.py </font>
```
usage: audio_extraction.py [-h] [-p PATH] [-x EXPORT]

Python script to extract audio from a video

options:
  -h, --help            show this help message and exit
  -p PATH, --path PATH  Target video path. Default is current directory
  -x EXPORT, --export EXPORT
                        Export folder. Default is current directory
```

## <font color="blue"> subtitles.py </font>

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

## <font color="blue"> video_export.py </font>

```
usage: video_export.py [-h] [-f IMAGES_FOLDER] [-a AUDIO_FILE] [-x EXPORT_FOLDER] [-s START] [-e END] [-v VOLUME_FACTOR]

Python script to merge several images into a video and add the audio to it

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

## <font color="blue"> video_images_extraction.py </font>

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

## <font color="blue"> cartoon.py </font>
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

## <font color="blue"> one_color_effect.py </font>
```
Python script to keep from an image some colors and set in black and white other colors

options:
  -h, --help            show this help message and exit
  -p IMAGE_PATH, --image-path IMAGE_PATH
                        Target image file
  -x EXPORT_FOLDER, --export-folder EXPORT_FOLDER
                        Folder where the edited image will be exported,
                        default is current directory
  -c [{RED,GREEN,BLUE,YELLOW,ORANGE,PINK,PURPLE,CYAN} ...], --colors [{RED,GREEN,BLUE,YELLOW,ORANGE,PINK,PURPLE,CYAN} ...]
                        Colors to keep
```