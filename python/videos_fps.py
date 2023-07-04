import os
import json
import cv2
import argparse

def get_fps(video_path):
    # Open the video
    video = cv2.VideoCapture(video_path)
    
    # Get the frames per second (FPS)
    fps = video.get(cv2.CAP_PROP_FPS)
    
    # Release the video
    video.release()
    
    return fps

def process_videos(directory):
    video_fps = {}
    
    # Iterate through the files in the directory
    for filename in os.listdir(directory):
        filepath = os.path.join(directory, filename)
        
        # Check if the file is a video
        if os.path.isfile(filepath) and filename.lower().endswith(('.mp4', '.avi', '.mkv')):
            # Get the FPS of the video
            fps = get_fps(filepath)
            
            # Add the information to the dictionary
            video_fps[filename] = fps
    
    return video_fps

def write_json(filepath, data):
    with open(filepath, 'w') as file:
        json.dump(data, file, indent=4)

def main():
    # Argument parser configuration
    parser = argparse.ArgumentParser(description="Python script to extract audio from a video")
    parser.add_argument("-p", "--path", help="Path to the directory containing the videos. Default is the current directory", default="./")
    parser.add_argument("-o", "--output", help="Output JSON file. Default is fps.json in current directory", default="./fps.json")
    args = parser.parse_args()

    # Video directory
    video_directory = args.path

    # Output filepath for the JSON file
    output_filepath = args.output

    output_directory = os.path.dirname(output_filepath)
    if not os.path.exists(output_directory):
        os.makedirs(output_directory)

    # Process videos and retrieve FPS
    video_fps = process_videos(video_directory)

    # Write the data to a JSON file
    write_json(output_filepath, video_fps)

if __name__ == "__main__":
    main()