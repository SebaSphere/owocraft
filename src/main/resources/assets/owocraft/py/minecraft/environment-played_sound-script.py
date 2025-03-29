import time
from dev.sebastianb.owocraft.client.facade import HapticVestInteraction
import random

print(soundEventName)
# print(worldAge)


def run_haptic_vest_interaction_skulk_click():
    upper_body_muscles = "Pectoral_R,Pectoral_L,Arm_R,Arm_L,Dorsal_R,Dorsal_L"
    lower_body_muscles = "Abdominal_R,Abdominal_L,Lumbar_R,Lumbar_L"

    hapticVestInteraction = HapticVestInteraction()

    start_time = time.time()
    duration = 4  # Total duration remains 4

    while True:
        print("MEOW")
        # Calculate elapsed time
        elapsed_time = time.time() - start_time

        intensity = max(0, 30 - (elapsed_time / duration) * 30)

        if elapsed_time <= 0.5:
            # First impact on lower back
            muscle_target = "Lumbar_R,Lumbar_L"
            hapticVestInteraction.runSensation("100,0.1,{:.2f},0,0,0,Impact".format(intensity), muscle_target)
        elif 0.5 < elapsed_time <= 1:
            intensity = max(0, 30 - ((elapsed_time) / 0.5) * 30)

            # Second impact on lower back
            muscle_target = "Lumbar_R,Lumbar_L"
            hapticVestInteraction.runSensation("100,0.1,{:.2f},0,0,0,Impact".format(intensity), muscle_target)
        elif 1 < elapsed_time <= 1.5:
            intensity = max(0, 30 - ((elapsed_time - 0.5) / 1) * 30)
            # First impact on upper back
            muscle_target = "Dorsal_R,Dorsal_L"
            hapticVestInteraction.runSensation("100,0.1,{:.2f},0,0,0,Impact".format(intensity), muscle_target)
        elif 1.5 < elapsed_time <= 2:
            intensity = max(0, 30 - ((elapsed_time - 1) / 1.5) * 30)
            # Second impact on upper back
            muscle_target = "Dorsal_R,Dorsal_L"
            hapticVestInteraction.runSensation("100,0.1,{:.2f},0,0,0,Impact".format(intensity), muscle_target)

        time.sleep(0.1)  # run 10 times a second

        # Break the loop if the duration has passed
        if elapsed_time > duration:
            break


if soundEventName == "minecraft:block.sculk_sensor.clicking":
    run_haptic_vest_interaction_skulk_click()
