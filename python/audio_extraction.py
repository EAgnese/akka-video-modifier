# from : https://www.thepythoncode.com/article/add-audio-to-video-in-python
# Import moviepy
import moviepy.editor
import sys
import argparse
import utils

def main():

    parser = argparse.ArgumentParser(description="Python script to extract audio from a video")
    parser.add_argument("-p", "--path", help="Target video path. Default is current directory", default="./")
    parser.add_argument("-x", "--export", help="Export folder. Default is current directory", default="./")

    args = parser.parse_args()
    video_path = args.path
    export_path:str = args.export
    export_path += "/" if not export_path.endswith("/") else ""


    video_name = video_path[video_path.rfind('/')+1:]

    #Load the Video
    video = moviepy.editor.VideoFileClip(video_path)

    #Extract the Audio
    audio = video.audio

    #Export the Audio
    utils.create_dir(f"{export_path}")
    audio.write_audiofile(export_path+"audio_" + video_name + ".wav")


if(__name__ == "__main__"):
    main()