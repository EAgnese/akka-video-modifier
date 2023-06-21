import cv2
import argparse
import os

def export_each_frame(video_path:str, export_path:str):
    """_summary_
    export each frame of a given video file to the export folder given in parameter
    Args:
        video_path (str): video file path
        export_path (str): export folder path
    """
    video_name = video_path[video_path.rfind('/')+1:].replace(".", "-") # get the video file name
    vidcap = cv2.VideoCapture(video_path)
    success,image = vidcap.read()
    count = 0
    if(not os.path.exists(f"{export_path}/{video_name}")):
        os.makedirs(f"{export_path}/{video_name}")
    while success:
        cv2.imwrite(f"{export_path}/{video_name}/{video_name}_frame{count}.png", image)     # save frame as PNG file
        success,image = vidcap.read()
        print(f'Read frame {count}: ', success)
        count += 1

def main():

    # make a command-line argument parser & add various parameters
    parser = argparse.ArgumentParser(description="Python script to export each frame of a given video")
    parser.add_argument("-v", "--video-path", help="Target video file")
    parser.add_argument("-x", "--export-folder", help="Folder where the frames of the vdeo will be exported, default is current directory", default=".")
    # parse the arguments

    args = parser.parse_args()
    video_path = args.video_path
    export_folder = args.export_folder

    export_each_frame(video_path, export_folder)
    print("export done.")

if(__name__ == "__main__"):
    main()