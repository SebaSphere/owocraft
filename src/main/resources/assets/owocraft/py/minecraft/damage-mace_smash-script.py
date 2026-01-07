import time
from dev.sebastianb.owocraft.client.facade import HapticVestInteraction
import random

hapticVestInteraction = HapticVestInteraction()

canRun = True

start_time = time.time()

duration = 2.0

while canRun:
    # Calculate elapsed time
    elapsed_time = time.time() - start_time

    # Apply the linear interpolation formula
    intensity = max(80, 100 - ((elapsed_time / total_transition_time) * (100 - 80)))

    canRun = hapticVestInteraction.runSensation("100,0.1,{:.2f},0,0,0,Impact".format(intensity), "Pectoral_R,Pectoral_L,Arm_R,Arm_L,Dorsal_R,Dorsal_L")

    # Break the loop if the duration has passed
    if elapsed_time > duration:
        canRun = False

    time.sleep(0.1)  # run 10 times a second
