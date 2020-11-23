Iteration 2
-----------

We added children photos where it made sense. Photos are shown in: 
1. The Children activity which lists children. Photos are shown beside the name of each child.
1. The Children edit activity. Shows the photo of the child being edited.
2. The Task activity which lists the different tasks. Photo of the next child for each task is shown beside the task.
2. The Task dialog fragment which inflates the information for a given task. Shows the photo of the next child for that task.
3. Flip Coin activity. Photo is shown of the next child who's turn it is to flip at the top of the activity.
4. Flip Coin override fragment. Photo of each child is shown in the flip queue.
5. Flip History activity. For each flip history entry, a photo is shown of the child corresponding to the entry.


With more UI elements being added, the layouts throughout the app were improved and resized to better display them. 
The Flip Coin activity layout was reworked to look the same on all screen sizes and change size dynamically.


The photos that the user puts for children are scaled down. This is so that the app can display many photos in a list
and not lag for a reasonable amount of entries.


We continued to be careful about our image sizes throughout the app and compressed all of them using TinyPNG (tinypng.com)
to reduce the size of our app.