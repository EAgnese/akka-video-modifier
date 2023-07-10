import cv2
import numpy as np
import argparse
import os

from enum import Enum

class Color(Enum):
    RED = ([0, 80, 50], [9, 255, 255], [170, 80, 50], [180, 255, 255])
    GREEN = ([35, 20, 20], [85, 255, 255], None, None)
    BLUE = ([95, 20, 20], [132, 255, 255], None, None)
    YELLOW = ([22, 80, 50], [34, 255, 255], None, None)
    ORANGE = ([10, 80, 50], [21, 255, 255], None, None)
    PINK = ([150, 80, 50], [169, 255, 255], None, None)
    PURPLE = ([133, 80, 50], [149, 255, 255], None, None)
    CYAN = ([86, 80, 50], [94, 255, 255], None, None)

    
    def get_all_colors():
        return [color.name for color in Color]


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
    color_names = args.colors
    colors = [Color[color_name] for color_name in color_names]


    image = cv2.imread(image_path)
    image_name = os.path.basename(image_path)

    image_export = os.path.join(export_folder, image_name)

    if len(colors) > 0:

        hsv = cv2.cvtColor(image, cv2.COLOR_BGR2HSV)
        mask = cv2.inRange(hsv, np.array([0, 0, 0]),np.array([0, 0, 0]))
        for color in colors:
            lower_color1, upper_color1, lower_color2, upper_color2 = color.value
            
            mask += cv2.inRange(hsv, np.array(lower_color1), np.array(upper_color1))
            # used for colors starting on top of red and ending on bottom of red in a hsv color circle
            # https://en.wikipedia.org/wiki/HSL_and_HSV
            if lower_color2 is not None and upper_color2 is not None:
                mask += cv2.inRange(hsv, np.array(lower_color2), np.array(upper_color2))
        

        image = apply_mask(image, mask)

    cv2.imwrite(image_export, image)

if __name__ == "__main__":
    main()