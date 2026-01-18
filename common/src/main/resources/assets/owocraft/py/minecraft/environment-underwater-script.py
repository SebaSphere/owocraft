import time
from dev.sebastianb.owocraft.client.facade import HapticVestInteraction
import math

muscle_target = "Pectoral_R,Pectoral_L,Abdominal_R,Abdominal_L,Arm_R,Arm_L,Dorsal_R,Dorsal_L,Lumbar_R,Lumbar_L"

hapticVestInteraction = HapticVestInteraction()

canRun = True

# Record the start time
start_time = time.time()

# Duration in seconds
duration = 0.5

while canRun:
    # Calculate elapsed time
    elapsed_time = time.time() - start_time

    # Calculate the result of cos wave
    intensity = math.cos(playerAge / 20) * 4 + 20
    # Run the haptic sensation with updated intensity
    canRun = hapticVestInteraction.runSensation("100,0.1,{:.2f},0,0,0,Impact".format(intensity), muscle_target)

    # Break the loop if the duration has passed
    if elapsed_time > duration:
        canRun = False

    time.sleep(0.1)  # run 10 times a second