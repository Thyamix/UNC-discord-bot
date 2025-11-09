# It's a discord bot

I really doubt anyone would have a use for this, I made it because I needed it.

If you do decide this is something you might need. Make sure to update the config.toml file and set "BOT_TOKEN" env var.

This bot was designed to help with Foxhole.
The idea is just to keep track of how long before the stockpile has to be refreshed. And send alerts to remind you to do so.

It is also likely more feature could arrive, at this point this is just for whatever I need, but if anyone needs anything else just make an issue and I may add it.

## Features

- /refresh command, refreshes the timer to the stockpile. This resest the timer back to 48 hours before expiry.
- /timeleft command, returns remaining time before stockpile expires as a discord timestamp.
- warnings for 1, 4, 12 hours before expiry. These also ping @role set in config file.
