# august 

Automatic GUI Testing Framework
-------------------------------

I started this project moved by the plight of QA folk who were driven to despair by the tedium of their thankless task in old days when the barbarian practice of testing by hand was still quite common. 

The project never progressed beyond proof-of-concept, I took a hiatus from computer science, and now that I'm back I see that commercial tools that automate GUI testing already exist. Still, I'll keep working on it, if only for practice.

Some tasks:
-----------

-Implement august.Option and august.OptionParses. (They existed once, but that was before the days of the cloud, when it was still possible to lose things)

-Decide whether to continue making it work for Swing or re-do for JavaFX... This will recurse into many tasks.

-Design an implement a utility that would generate and save test scripts by recording actions of the QA engineer. (This is non-trivial: August would record the underlying object the user clicked/dragged/etc., not simply the x & y coordinates. At least it was non-trivial in Swing from what I recall).

-Set up version control repository (git?) integration, so the GUI tests can run automatically.
