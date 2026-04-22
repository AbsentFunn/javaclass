# Space Invaders: Enhanced Edition

A modern take on the classic arcade shooter with procedural visual effects, dynamic powerups, and advanced enemy AI.

## Controls
- **Left/Right Arrow Keys**: Move the player ship across the bottom of the screen.
- **Spacebar**: Fire the player's primary cannon (single-shot limit).
- **Enter**: Restart the game from the Game Over screen.

## Core Gameplay Mechanics
- **Objective**: Eliminate all aliens in the grid formation to progress to the next level.
- **Lives**: You start with 3 lives. The game ends if your lives reach zero or if the aliens descend to your ship's altitude.
- **Scoring**: 10 points per alien destroyed (20 points during Double Score mode).
- **Level Scaling**: Each level increases the base horizontal speed of the alien formation, making them harder to hit and faster to descend.

## Powerup System
Powerups spawn every 45-60 seconds and fall from the top of the screen.
- **Extra Life (Green, 'L')**: Instantly grants one additional life.
- **Double Score (Gold, '$')**: Activating this gives a 15-second window where all kills are worth 2x points.
- **Shield (Blue, 'S')**: Grants a 15-second protective barrier. While active, alien bullets pass through you without depleting lives.
- **Visual Feedback**:
    - **Aura**: A color-coded pulsing aura (Green, Gold, or Blue) surrounds your ship while a powerup is active.
    - **Popups**: A scale-animated translucent popup appears in the center of the screen when a powerup is collected.

## Alien Combat & AI
Aliens fire two distinct types of projectiles:
- **Regular Bullets**: Orange vertical pulses that travel in a straight line.
- **Homing Bullets**: Large, dark-red projectiles with a glowing aura.
    - **Tracking Logic**: These bullets actively steer toward the player's current X/Y coordinates.
    - **Trajectory Locking**: To ensure the game remains fair, homing bullets "lock" their angle once they are within 180 pixels of the player, allowing you to dodge them with a well-timed move.

## Visual Effects (FX)
- **Galaxy Background**: An animated, multi-layered space environment:
    - **Drifting Nebulae**: Procedural purple, blue, and magenta clouds that slowly shift using trigonometric pathing.
    - **Parallax Stars**: 100 stars moving at varied speeds to create an illusion of depth, featuring individual "twinkle" animations.
    - **Transparency**: The background is rendered at 70% opacity over a solid black base to provide atmosphere without distracting from gameplay.
- **Screen Shake**: Intense high-frequency shaking occurs when the player is hit; a milder shake occurs when aliens explode.
- **Muzzle Flash**: A bright yellow burst appears at the ship's tip during firing.
- **Explosions**: Radial bursts of orange and white particles with fade-out transparency.

## Sound Engine
The game features a custom synthesized audio engine providing zero-latency sound effects:
- **Fire**: A high-frequency sweep.
- **Explosion**: A deep, low-pass filtered rumble.
- **Player Hit**: A descending metallic sweep.
- **Level Up**: An ascending three-note major melody.
- **Powerup**: A distinct bright double-tone melody.
