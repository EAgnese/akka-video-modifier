import cv2
import numpy as np
import argparse
import os

from enum import Enum

class Color(Enum):
    RED = 0
    GREEN = 1
    BLUE = 2
    YELLOW = 3
    ORANGE = 4
    PINK = 5
    PURPLE = 6
    CYAN = 7

    def get_color(color_name_list):
        l = []
        for name in color_name_list:
            if name == "RED":
                l.append(Color.RED)
            if name == "GREEN":
                l.append(Color.GREEN)
            if name == "BLUE":
                l.append(Color.BLUE)
            if name == "YELLOW":
                l.append(Color.YELLOW)
            if name == "ORANGE":
                l.append(Color.ORANGE)
            if name == "PINK":
                l.append(Color.PINK)
            if name == "PURPLE":
                l.append(Color.PURPLE)
            if name == "CYAN":
                l.append(Color.CYAN)
        return l
    
    def get_all_colors():
        return ["RED", "GREEN", "BLUE", "YELLOW", "ORANGE", "PINK", "PURPLE", "CYAN"]


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
    parser.add_argument("-c", "--colors", help="Colors to keep", default="None", choices=Color.get_all_colors(), nargs="+")
    # parse the arguments

    args = parser.parse_args()
    image_path = args.image_path
    export_folder = args.export_folder
    color_name = args.colors
    colors = Color.get_color(color_name)


    image = cv2.imread(image_path)
    image_name = os.path.basename(image_path)

    image_export = os.path.join(export_folder, image_name)

    if len(colors) > 0:

        hsv = cv2.cvtColor(image, cv2.COLOR_BGR2HSV)
        mask = cv2.inRange(hsv, np.array([0, 0, 0]),np.array([0, 0, 0]))
        if Color.BLUE in colors:
            lower_blue = np.array([95, 20, 20])
            upper_blue = np.array([132, 255, 255])
            mask += cv2.inRange(hsv, lower_blue, upper_blue)
            # image = apply_mask(image, mask)

        if Color.GREEN in colors:
            lower_green = np.array([35, 20, 20])
            upper_green = np.array([85, 255, 255])
            mask += cv2.inRange(hsv, lower_green, upper_green)
            # image = apply_mask(image, mask)

        if Color.RED in colors:
            lower_redorange = np.array([0, 80, 50])
            upper_redorange = np.array([9, 255, 255])
            lower_pinkred = np.array([170, 80, 50])
            upper_pinkred = np.array([180, 255, 255])
            mask += cv2.inRange(hsv, lower_redorange, upper_redorange)
            mask += cv2.inRange(hsv, lower_pinkred, upper_pinkred)
            # mask = maskREDtoORANGE + maskPINKtoRED

        if Color.YELLOW in colors:
            lower_yellow = np.array([22, 80, 50])
            upper_yellow = np.array([34, 255, 255])
            mask += cv2.inRange(hsv, lower_yellow, upper_yellow)
        
        if Color.ORANGE in colors:
            lower_orange = np.array([10, 80, 50])
            upper_orange = np.array([21, 255, 255])
            mask += cv2.inRange(hsv, lower_orange, upper_orange)
        
        if Color.PINK in colors:
            lower_pink = np.array([150, 80, 50])
            upper_pink = np.array([169, 255, 255])
            mask += cv2.inRange(hsv, lower_pink, upper_pink)

        if Color.PURPLE in colors:
            lower_purple = np.array([133, 80, 50])
            upper_purple = np.array([149, 255, 255])
            mask += cv2.inRange(hsv, lower_purple, upper_purple)

        if Color.CYAN in colors:
            lower_purple = np.array([86, 80, 50])
            upper_purple = np.array([94, 255, 255])
            mask += cv2.inRange(hsv, lower_purple, upper_purple)

        image = apply_mask(image, mask)

    cv2.imwrite(image_export, image)

if __name__ == "__main__":
    main()