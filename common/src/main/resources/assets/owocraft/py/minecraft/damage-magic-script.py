import time
import random
from dev.sebastianb.owocraft.client.facade import HapticVestInteraction

muscles = ["Pectoral_R", "Pectoral_L", "Abdominal_R", "Abdominal_L", "Arm_R", "Arm_L", "Dorsal_R", "Dorsal_L", "Lumbar_R",
           "Lumbar_L"]

muscle_target_one = random.choice(muscles)
muscles.remove(muscle_target_one)
muscle_target_one += "," + random.choice(muscles)
muscles.remove(muscle_target_one.split(",")[-1])
muscle_target_one += "," + random.choice(muscles)

muscle_target_two = random.choice(muscles)
muscles.remove(muscle_target_two)
muscle_target_two += "," + random.choice(muscles)
muscles.remove(muscle_target_two.split(",")[-1])
muscle_target_two += "," + random.choice(muscles)

muscle_target_three = random.choice(muscles)
muscles.remove(muscle_target_three)
muscle_target_three += "," + random.choice(muscles)
muscles.remove(muscle_target_three.split(",")[-1])
muscle_target_three += "," + random.choice(muscles)

hapticVestInteraction = HapticVestInteraction()

canRun = True
intensity = 100

# Record the start time
start_time = time.time()

# Duration in seconds
duration = 0.9

while canRun:
    elapsed_time = time.time() - start_time



    # Calculate elapsed time
    elapsed_time = time.time() - start_time
    if elapsed_time > 0.6:
        intensity = max(0, 30 - (elapsed_time - 0.6 / duration - 0.6) * 30)
        canRun = hapticVestInteraction.runSensation("100,0.1,{:.2f},0,0,0,Impact".format(intensity), muscle_target_three)
    if elapsed_time > 0.3:
        intensity = max(0, 30 - (elapsed_time - 0.3 / duration - 0.3) * 30)
        canRun = hapticVestInteraction.runSensation("100,0.1,{:.2f},0,0,0,Impact".format(intensity), muscle_target_two)
    elif elapsed_time > 0:
        intensity = max(0, 30 - (elapsed_time / duration - 0.6) * 30)
        canRun = hapticVestInteraction.runSensation("100,0.1,{:.2f},0,0,0,Impact".format(intensity), muscle_target_one)


    # Break the loop if the duration has passed
    if elapsed_time > duration:
        canRun = False

    time.sleep(0.1)  # run 10 times a second