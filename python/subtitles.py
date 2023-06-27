import cv2
import numpy as np
import sys
import utils
import argparse
import os


class Subtitles:
    """
    The Subtitles class defines methods for calculating the size and placement of text on an image, and
    for adding text to an image.
    """
    def __init__(self, text:str, image_path:str, font_scale:int, thickness:int, color:tuple[int, int, int], bottom_padding_percentage, line_spacing:int, font:int) -> None:
        self.text = text
        self.font_scale = font_scale
        self.thickness = thickness
        self.color = color
        self.image_path = image_path
        self.font = font
        self.bottom_padding_percentage = bottom_padding_percentage
        self.line_spacing = line_spacing


    def nbLine(self) -> int:
        """
        returns the number of lines in the subtitle text, separated by a new line character.
        """
        return self.text.count('\n') + 1

    def getTextSize(self) -> tuple[int, int]:
        """
        This function returns the size of the text based on the font, font scale, and thickness.
        :return: A tuple of two integers representing the size of the text when rendered with the
        specified font, font scale, and thickness. The first integer represents the width and the second
        integer represents the height.
        """
        return cv2.getTextSize(self.text, self.font, self.font_scale, self.thickness)[0]

    def getTextBlockHeight(self) -> int:
        """
        This function calculates the height of a text block based on the size of the text and the number
        of lines.
        :return: the total height of a text block based on the height of one line of text, the number of
        lines in the block, and the spacing between lines.
        """
        text_height = self.getTextSize()[1]
        return text_height*self.nbLine() + self.line_spacing*(self.nbLine()-1)

    def getTextBlockStarting(self, image) -> int:
        """
        This function returns the starting position of a text block in an image based on its shape and
        padding percentage.
        
        :param image: The input image for which the function is being called
        :return: an integer value which represents the starting position of the text block in the given
        image. The starting position is calculated based on the height of the image, the bottom padding
        percentage, and the height of the text block.
        """
        return int(image.shape[0] - image.shape[0]*self.bottom_padding_percentage/100 - self.getTextBlockHeight())

    def getHorizontalPlacementForLine(self, line:str, image) -> int:
        """
        This function calculates the horizontal placement of a given line of text in an image.
        
        :param line: A string representing the text that needs to be placed horizontally on the image
        :type line: str
        :param image: The image parameter is a variable that represents the image on which the text will
        be placed.
        :return: an integer value which represents the horizontal placement of the given line on the
        image.
        """
        line_length = cv2.getTextSize(line, self.font, self.font_scale, self.thickness)[0][0]
        return int((image.shape[1]/2) - (line_length/2))
    
    def getVerticalPlacementForLine(self, line_id:int, image) -> int:
        """
        This function calculates the vertical placement of a line of text based on its line ID, image,
        and text block size.
        
        :param line_id: line_id is an integer representing the index of the line for which we want to
        calculate the vertical placement. It is used in the calculation of the vertical position of the
        line based on the size of the text block and the line spacing
        :type line_id: int
        :param image: The image parameter is a variable that represents the image on which the text will
        be placed.
        :return: an integer value which represents the vertical placement of the given line on the
        image.
        """
        return int(self.getTextBlockStarting(image) + line_id*(self.getTextSize()[1] + self.line_spacing))
    
    def putTextOn(self, image):
        """
        This function puts text on an image at specified horizontal and vertical placements.
        
        :param image: The image on which the text is to be placed
        :return: an image with the text added to it.
        """
        for i, line in enumerate(self.text.split('\n')):
            org = (
                self.getHorizontalPlacementForLine(line, image), # horizaontal placement
                self.getVerticalPlacementForLine(i, image) # vertical placement
            )
            image = cv2.putText(image, line, org, self.font, self.font_scale, (0, 0, 0), self.thickness + 5, cv2.LINE_AA)
            image = cv2.putText(image, line, org, self.font, self.font_scale, self.color, self.thickness, cv2.LINE_AA)

        return image


def main():
    # make a command-line argument parser & add various parameters
    parser = argparse.ArgumentParser(description="Python script to add subtitles to an image")
    parser.add_argument("-p", "--image-path", help="Target image file")
    parser.add_argument("-s", "--subtitles", help="Subtitles added to image", default="")
    parser.add_argument("-x", "--export-folder", help="Folder where the edited image will be exported, default is current directory", default=".")
    # parse the arguments

    args = parser.parse_args()
    image_path = args.image_path
    export_folder = args.export_folder
    subtitles = args.subtitles

    # image name is the name after the last "/"
    img_name = image_path[image_path.rfind('/')+1:]

    subtitles = subtitles.replace("\\n", '\n')

    sub = Subtitles(
        text = subtitles,
        image_path=image_path,
        font_scale=1,
        thickness=2,
        color=(255, 255, 255),
        bottom_padding_percentage=5,
        line_spacing = 10,
        font=cv2.FONT_HERSHEY_SIMPLEX
    )
    # Reading an image in default mode
    image = cv2.imread(sub.image_path)

    # resizing image
    # width = 600
    # image = cv2.resize(image, (width, int(image.shape[0]/int(image.shape[1])*width)))


    image = sub.putTextOn(image)

    
    # Displaying the image
    # TODO : delete
    # utils.show_image(image)
    os.remove(image_path)
    cv2.imwrite(export_folder + "/" + img_name, image)

if(__name__ == "__main__"):
    main()