import time
from dev.sebastianb.owocraft.client.facade import HapticVestInteraction
import random


def run_haptic_vest_interaction_large_hands():

    muscles = "Pectoral_R,Pectoral_L", "Arm_R,Arm_L", "Dorsal_R,Dorsal_L"
    hapticVestInteraction = HapticVestInteraction()

    canRun = True

    # Record the start time
    start_time = time.time()

    # Duration in seconds
    duration = 0.5

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

def run_haptic_vest_interaction_slash():
    upper_body_muscles = "Pectoral_R,Pectoral_L,Arm_R,Arm_L,Dorsal_R,Dorsal_L"
    lower_body_muscles = "Abdominal_R,Abdominal_L,Lumbar_R,Lumbar_L"

    muscle_target = upper_body_muscles

    hapticVestInteraction = HapticVestInteraction()

    canRun = True

    start_time = time.time()

    duration = 0.7

    while canRun:
        # Calculate elapsed time
        elapsed_time = time.time() - start_time

        # Set the initial and final intensities and the total time for the transition
        initial_intensity = 50
        final_intensity = 20
        total_transition_time = 0.3

        # Check if the elapsed time is within the transition period
        if 0 <= elapsed_time <= total_transition_time:
            # Apply the linear interpolation formula
            intensity = initial_intensity - ((elapsed_time / total_transition_time) * (initial_intensity - final_intensity))
            muscle_target = upper_body_muscles
        elif 0.3 <= elapsed_time <= 0.7:
            initial_intensity_2 = 30
            final_intensity_2 = 0
            total_transition_time_2 = 0.7 - 0.3

            intensity = initial_intensity_2 - ((elapsed_time - 0.3) / total_transition_time_2) * (
                    initial_intensity_2 - final_intensity_2)
            muscle_target = lower_body_muscles
        else:
            intensity = 0

        canRun = hapticVestInteraction.runSensation("100,0.1,{:.2f},0,0,0,Impact".format(intensity), muscle_target)

        # Break the loop if the duration has passed
        if elapsed_time > duration:
            canRun = False

        time.sleep(0.1)  # run 10 times a second

def run_haptic_vest_interaction_hands():

    muscles = "Pectoral_R,Pectoral_L", "Arm_R,Arm_L", "Dorsal_R,Dorsal_L"
    hapticVestInteraction = HapticVestInteraction()

    canRun = True

    # Record the start time
    start_time = time.time()

    # Duration in seconds
    duration = 0.5

    random_muscle = random.choice(muscles)

    while canRun:
        # Calculate elapsed time
        elapsed_time = time.time() - start_time

        # Linearly scale intensity based on elapsed time
        intensity = max(0, 40 - (elapsed_time / duration) * 40)

        # Run the haptic sensation with updated intensity
        canRun = hapticVestInteraction.runSensation(
            "100,0.1,{:.2f},0,0,0,Impact".format(intensity), random_muscle
        )

        # Break the loop if the duration has passed
        if elapsed_time > duration:
            canRun = False

        time.sleep(0.1)  # run 10 times a second


if damageFromEntityType in ["entity.minecraft.warden", "entity.minecraft.iron_golem", "entity.minecraft.ender_dragon"]:
    run_haptic_vest_interaction_large_hands()
else:
    if weaponType in ["sword", "axe"]:
        run_haptic_vest_interaction_slash()
    else:
        run_haptic_vest_interaction_hands()
