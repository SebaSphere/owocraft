import time
import random
from dev.sebastianb.owocraft.client.facade import HapticVestInteraction

muscle_target = random.choice(
    ["Pectoral_R", "Pectoral_L", "Abdominal_R", "Abdominal_L", "Arm_R", "Arm_L", "Dorsal_R", "Dorsal_L", "Lumbar_R",
     "Lumbar_L"])

hapticVestInteraction = HapticVestInteraction()


canRun = True
intensity = 100

# Record the start time
start_time = time.time()

# Duration in seconds
duration = 1.2

while canRun:
    # Calculate elapsed time
    elapsed_time = time.time() - start_time

    # Linearly scale intensity based on elapsed time
    intensity = max(0, 100 - (elapsed_time / duration) * 100)
    # Run the haptic sensation with updated intensity
    canRun = hapticVestInteraction.runSensation("100,0.1,{:.2f},0,0,0,Impact".format(intensity), muscle_target)

    # Break the loop if the duration has passed
    if elapsed_time > duration:
        canRun = False

    time.sleep(0.1)  # run 10 times a second