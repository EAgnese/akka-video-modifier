import cv2
import os

def show_image(img):
    cv2.imshow("test", img)
    wait_time = 1000
    while cv2.getWindowProperty('test', cv2.WND_PROP_VISIBLE) >= 1:
        keyCode = cv2.waitKey(wait_time)
        if (keyCode & 0xFF) == ord("q"):
            cv2.destroyAllWindows()
            break

def create_dir(dir):
    if(not os.path.exists(dir)):
        os.makedirs(dir)