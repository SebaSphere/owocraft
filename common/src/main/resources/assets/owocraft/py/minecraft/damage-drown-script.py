import time
from dev.sebastianb.owocraft.client.facade import HapticVestInteraction
import math

muscle_target = "Pectoral_R,Pectoral_L,Abdominal_R,Abdominal_L,Arm_R,Arm_L,Dorsal_R,Dorsal_L,Lumbar_R,Lumbar_L"

hapticVestInteraction = HapticVestInteraction()

canRun = True

start_time = time.time()

duration = 1

while canRun:
    # Calculate elapsed time
    elapsed_time = time.time() - start_time


    # make it so intensity goes up and down twice
    intensity = 0
    # Set the initial and final intensities and the total time for the transition
    initial_intensity = 20
    final_intensity = 10
    total_transition_time = 0.4

    # Check if the elapsed time is within the transition period
    if 0 <= elapsed_time <= total_transition_time:
        # Apply the linear interpolation formula
        intensity = initial_intensity - ((elapsed_time / total_transition_time) * (initial_intensity - final_intensity))

    elif 0.4 <= elapsed_time <= 0.8:
        initial_intensity_2 = 20
        final_intensity_2 = 10
        total_transition_time_2 = 0.8 - 0.4

        intensity = initial_intensity_2 - ((elapsed_time - 0.4) / total_transition_time_2) * (
                    initial_intensity_2 - final_intensity_2)
    else:
        intensity = 0

    canRun = hapticVestInteraction.runSensation("100,0.1,{:.2f},0,0,0,Impact".format(intensity), muscle_target)

    # Break the loop if the duration has passed
    if elapsed_time > duration:
        canRun = False

    time.sleep(0.1)  # run 10 times a second