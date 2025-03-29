import time
from dev.sebastianb.owocraft.client.facade import HapticVestInteraction
import random


def run_haptic_vest_interaction_pearl():
    all_muscles = "Pectoral_R,Pectoral_L,Arm_R,Arm_L,Dorsal_R,Dorsal_L,Abdominal_R,Abdominal_L,Lumbar_R,Lumbar_L"

    muscle_target = all_muscles

    hapticVestInteraction = HapticVestInteraction()

    canRun = True

    start_time = time.time()

    duration = 0.7

    while canRun:
        # Calculate elapsed time
        elapsed_time = time.time() - start_time

        # Set the initial and final intensities and the total time for the transition
        initial_intensity = 50
        final_intensity = 0
        total_transition_time = 0.5

        # Check if the elapsed time is within the transition period
        if 0 <= elapsed_time <= total_transition_time:
            # Apply the linear interpolation formula
            intensity = initial_intensity - ((elapsed_time / total_transition_time) * (initial_intensity - final_intensity))
            muscle_target = all_muscles
        elif 0.5 <= elapsed_time <= 1:
            initial_intensity_2 = 30
            final_intensity_2 = 0
            total_transition_time_2 = 1 - 0.5

            intensity = initial_intensity_2 - ((elapsed_time - 0.5) / total_transition_time_2) * (
                    initial_intensity_2 - final_intensity_2)
            muscle_target = all_muscles
        else:
            intensity = 0

        canRun = hapticVestInteraction.runSensation("100,0.1,{:.2f},0,0,0,Impact".format(intensity), muscle_target)

        # Break the loop if the duration has passed
        if elapsed_time > duration:
            canRun = False

        time.sleep(0.1)  # run 10 times a second

run_haptic_vest_interaction_pearl()