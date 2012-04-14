Best solution is circle-blowing algorithm, which divides the problem into two:
1. Choose "good" sensor layout.
2. Given layout of the sensors, increase the circles until optimal (local optimal?) is reached.

Things to improve:
1. Better layout algorithm. 
Pre-calculated the first 9 layouts, but the rest are sub-optimal.
2. Better increment strategy (right now, increasing size by 10% of the field size to achieve scalable performances. 
Increasing it by single step yields better results - ~ 10% better, but decrease performances by factor of 10. Can increase the circles by 10, and start to perform "single step" search at the "end".
3. Better circle increment strategy. 
Right now, picking the smallest one. Order may affect the results, and the "smallest one" strategy is not deterministic - two different runs may yield different results, due to different choises. Probably picking cycle who's edge is nearest to the CH of the largest group will yield deterministic and better results.
4. GUI can always be improved
  