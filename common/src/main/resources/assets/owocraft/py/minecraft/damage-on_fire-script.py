import time
from dev.sebastianb.owocraft.client.facade import HapticVestInteraction

# This has regenerated

hapticVestInteraction = HapticVestInteraction()

canRun = True

# Record the start time
start_time = time.time()

# Duration in seconds
duration = 0.3

muscle_target = "Abdominal_R,Abdominal_L,Lumbar_R,Lumbar_L"


while canRun:
    # Calculate elapsed time
    elapsed_time = time.time() - start_time

    # Linearly scale intensity based on elapsed time
    intensity = max(20, 30 - (elapsed_time / duration) * 30)

    # Run the haptic sensation with updated intensity
    canRun = hapticVestInteraction.runSensation("100,0.1,{:.2f},0,0,0,Impact".format(intensity), muscle_target)

    # Break the loop if the duration has passed
    if elapsed_time > duration:
        canRun = False

    time.sleep(0.1)  # run 10 times a second
