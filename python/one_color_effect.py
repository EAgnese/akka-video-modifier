import cv2
import numpy as np
import argparse
import os

from enum import Enum

class Color(Enum):
    RED = 0
    GREEN = 1
    BLUE = 2
    NONE = 3

    def get_color(name):
        if name == "RED":
            return Color.RED
        if name == "GREEN":
            return Color.GREEN
        if name == "BLUE":
            return Color.BLUE
        return None


def apply_mask(image, mask):
    
    filtered_image = cv2.bitwise_and(image, image, mask=mask)
    non_filtered_pixels = cv2.bitwise_not(mask)
    non_filtered_grayscale = cv2.cvtColor(cv2.bitwise_and(image, image, mask=non_filtered_pixels), cv2.COLOR_BGR2GRAY)
    result = cv2.bitwise_or(filtered_image, cv2.cvtColor(non_filtered_grayscale, cv2.COLOR_GRAY2BGR))
    return result

def main():
    # make a command-line argument parser & add various parameters
    parser = argparse.ArgumentParser(description="Python script to keep only one color and set in black and white other colors")
    parser.add_argument("-p", "--image-path", help="Target image file")
    parser.add_argument("-x", "--export-folder", help="Folder where the edited image will be exported, default is current directory", default=".")
    parser.add_argument("-c", "--color", help="Color to keep. Choices : [RED, GREEN, BLUE]", default="None")
    # parse the arguments

    args = parser.parse_args()
    image_path = args.image_path
    export_folder = args.export_folder
    color_name = args.color
    color = Color.get_color(color_name)


    image = cv2.imread(image_path)
    image_name = os.path.basename(image_path)

    image_export = os.path.join(export_folder, image_name)

    if color != None:

        hsv = cv2.cvtColor(image, cv2.COLOR_BGR2HSV)

        if color == Color.BLUE:
            lower_blue = np.array([80, 20, 20])
            upper_blue = np.array([132, 255, 255])
            mask = cv2.inRange(hsv, lower_blue, upper_blue)
            image = apply_mask(image, mask)

        if color == Color.GREEN:
            lower_green = np.array([35, 20, 20])
            upper_green = np.array([85, 255, 255])
            mask = cv2.inRange(hsv, lower_green, upper_green)
            image = apply_mask(image, mask)

        if color == Color.RED:
            lower_redorange = np.array([0, 80, 50])
            upper_redorange = np.array([5, 255, 255])
            lower_pinkred = np.array([160, 80, 50])
            upper_pinkred = np.array([180, 255, 255])
            maskREDtoORANGE = cv2.inRange(hsv, lower_redorange, upper_redorange)
            maskPINKtoRED = cv2.inRange(hsv, lower_pinkred, upper_pinkred)
            mask = maskREDtoORANGE + maskPINKtoRED
            image = apply_mask(image, mask)

    cv2.imwrite(image_export, image)

if __name__ == "__main__":
    main()