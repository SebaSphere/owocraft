import time
from dev.sebastianb.owocraft.client.facade import HapticVestInteraction
import math

muscle_target = "Arm_R,Arm_L,Dorsal_R,Dorsal_L,Lumbar_R,Lumbar_L"

hapticVestInteraction = HapticVestInteraction()

canRun = True

start_time = time.time()

duration = 0.3

while canRun:
    # Calculate elapsed time
    elapsed_time = time.time() - start_time

    # the 0.8 is relevant because it's the hardcoded speed when sensations should start
    intensity = (playerSpeed * 2 - (0.8 + 0.1)) * 20

    # Run the haptic sensation with updated intensity
    canRun = hapticVestInteraction.runSensation("100,0.1,{:.2f},0,0,0,Impact".format(intensity), muscle_target)

    # Break the loop if the duration has passed
    if elapsed_time > duration:
        canRun = False

    time.sleep(0.1)  # run 10 times a second