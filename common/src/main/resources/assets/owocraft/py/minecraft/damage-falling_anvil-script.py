import time
from dev.sebastianb.owocraft.client.facade import HapticVestInteraction
import random


muscles = "Pectoral_R,Pectoral_L", "Arm_R,Arm_L", "Dorsal_R,Dorsal_L"
hapticVestInteraction = HapticVestInteraction()

canRun = True

# Record the start time
start_time = time.time()

# Duration in seconds
duration = 0.9

random_muscle = random.choice(muscles)

while canRun:
    # Calculate elapsed time
    elapsed_time = time.time() - start_time

    # Linearly scale intensity based on elapsed time
    intensity = max(0, 70 - (elapsed_time / duration) * 70)

    # Run the haptic sensation with updated intensity
    canRun = hapticVestInteraction.runSensation(
        "100,0.1,{:.2f},0,0,0,Impact".format(intensity), random_muscle
    )

    # Break the loop if the duration has passed
    if elapsed_time > duration:
        canRun = False

    time.sleep(0.1)  # run 10 times a second