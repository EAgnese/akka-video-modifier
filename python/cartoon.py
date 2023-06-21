import cv2
import numpy as np
import time
import sys
import utils
import argparse



def edge_mask(img, line_size, blur_value):
    gray = cv2.cvtColor(img, cv2.COLOR_BGR2GRAY)
    gray_blur = cv2.medianBlur(gray, blur_value)
    edges = cv2.adaptiveThreshold(gray_blur, 255, cv2.ADAPTIVE_THRESH_MEAN_C, cv2.THRESH_BINARY, line_size, blur_value)
    return edges

def color_quantization(img, k):
    # Transform the image
    data = np.float32(img).reshape((-1, 3))

    # Determine criteria
    criteria = (cv2.TERM_CRITERIA_EPS + cv2.TERM_CRITERIA_MAX_ITER, 20, 0.001)

    # Implementing K-Means
    ret, label, center = cv2.kmeans(data, k, None, criteria, 10, cv2.KMEANS_RANDOM_CENTERS)
    center = np.uint8(center)
    result = center[label.flatten()]
    result = result.reshape(img.shape)
    return result

def main():

    # make a command-line argument parser & add various parameters
    parser = argparse.ArgumentParser(description="Python script to add cartoon filter to image")
    parser.add_argument("-p", "--image-path", help="Target image file")
    parser.add_argument("-x", "--export-folder", help="Folder where the edited image will be exported, default is current directory", default=".")
    # parse the arguments

    args = parser.parse_args()
    image_path = args.image_path
    export_folder = args.export_folder

    img_name = image_path[image_path.rfind('/')+1:]
    # load image
    img = cv2.imread(image_path)
    print(f"image {image_path} loaded")
    # ==============
    # TODO : delete
    # width = 900
    # img = cv2.resize(img, (width, int(img.shape[0]/int(img.shape[1])*width)))
    # utils.show_image(img)
    # ==============

    line_size = 11
    blur_value = 9
    edges = edge_mask(img, line_size, blur_value)
    print(f"edges calculated")
    # TODO : delete
    # utils.show_image(edges)

    total_color = 31
    img = color_quantization(img, total_color)
    print(f"color quantization done")
    # TODO : delete
    # utils.show_image(img)

    blurred = cv2.bilateralFilter(img, d=15, sigmaColor=1,sigmaSpace=1)

    cartoon = cv2.bitwise_and(blurred, blurred, mask=edges)
    print(f"final cartoon image done")
    # TODO : delete
    # utils.show_image(cartoon)
    cv2.imwrite(export_folder+ "/" + img_name, cartoon)

if __name__ == "__main__":
    main()