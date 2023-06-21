import cv2
import numpy as np
import glob
import re
import os

from moviepy.editor import VideoFileClip, AudioFileClip
import argparse

def main():

    # make a command-line argument parser & add various parameters
    parser = argparse.ArgumentParser(description="Python script to add audio to video clip")
    parser.add_argument("-f", "--images-folder", help="Target images folder to create video with")
    parser.add_argument("-a", "--audio-file", help="Target audio file to embed with the video")
    parser.add_argument("-x", "--export-folder", help="Folder where the vdeo will be exported, default is current directory", default=".")
    parser.add_argument("-s", "--start", help="Start duration of the audio file, default is 0", default=0, type=int)
    parser.add_argument("-e", "--end", help="The end duration of the audio file, default is the length of the video file", type=int)
    parser.add_argument("-v", "--volume-factor", type=float, default=1.0, help="The volume factor to multiply by the volume of the audio file, 1 means no change, below 1 will decrease volume, above will increase.")
    # parse the arguments
    args = parser.parse_args()
    images_folder = args.images_folder
    audio_file = args.audio_file
    export_folder = args.export_folder
    start = args.start
    end = args.end
    volume_factor = args.volume_factor
    # print the passed parameters, just for logging
    print(vars(args))






    img_array = []
    numbers = re.compile(r'(\d+)')
    def numericalSort(value):
        parts = numbers.split(value)
        parts[1::2] = map(int, parts[1::2])
        return parts

    for filename in sorted(glob.glob(images_folder + '/*.jpg'), key=numericalSort):
        img = cv2.imread(filename)
        height, width, layers = img.shape
        size = (width,height)
        img_array.append(img)
    
    
    out = cv2.VideoWriter(images_folder+'_TEMP_video.avi',cv2.VideoWriter_fourcc(*'DIVX'), 12, size)
    
    for i in range(len(img_array)):
        out.write(img_array[i])
    out.release()


    # load the video
    video_clip = VideoFileClip(images_folder+'_TEMP_video.avi')
    # load the audio
    audio_clip = AudioFileClip(audio_file)
    # use the volume factor to increase/decrease volume
    audio_clip = audio_clip.volumex(volume_factor)

    # if end is not set, use video clip's end
    if not end:
        end = video_clip.end
    # make sure audio clip is less than video clip in duration
    # setting the start & end of the audio clip to `start` and `end` paramters
    final_audio = audio_clip.subclip(start, end)

    # add the final audio to the video
    final_clip = video_clip.set_audio(final_audio)

    # save the final clip
    final_clip.write_videofile(export_folder+"/"+ images_folder.replace("\\", "_") + "_final.mp4")


    os.remove(images_folder+'_TEMP_video.avi')


if(__name__ == "__main__"):
    main()