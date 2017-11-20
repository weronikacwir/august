# august 

Automatic GUI Testing Framework
-------------------------------

I started this project moved by the plight of QA folk who were driven to despair by the tedium of their thankless task in the old days, when the barbarian practice of testing by hand was still quite common. 

August takes xml scripts that specify a user interaction by listing a sequence of GUI components-action tuples (GUI actions), and the desired outcomes as component-property-expected value tuples (GUI result checks). It starts the application to be tested, generates the input events specified in the test scripts (actually moving the mouse on screen using java.awt.Robot), and logs the results.

The motivation was to free humans from performing the same test by hand more than once. Some people who worked with me at KL Group/ Sitraka / Quest generously contributed code to this pet project of mine. But it never progressed beyond proof-of-concept by the time I took a long hiatus from computer science.  And now that I'm back I see that commercial tools that automate GUI testing already exist. Still, I'll keep working on it when I have time, if only to practice concurrent programming, reflection, OO patterns, etc.


Getting Started
---------------
At the moment all you can do is look at the code. TestEngine.java is the heart of August - start there.

(Note to self: to make August work, implement missing parts of Option & OptionParser; find a simple Swing application on which August can practice, and write an xml test script for it.)


Some Tasks
-----------

-- Implement august.Option and august.OptionParser. (They existed once, but that was before the days of the cloud, when it was still possible to lose things.)

-- Decide whether to stick with XML for scripts... has something better come along?

-- Decide whether to continue making it work for Swing or re-do for JavaFX... This will recurse into many tasks.

-- Design an implement a utility that would generate and save test scripts by recording actions of the QA engineer. (This is non-trivial: August would record the underlying object the user clicked/dragged/etc., not simply the x & y coordinates. At least it was non-trivial in Swing from what I recall).

-- Set up version control repository (git?) integration, so the GUI tests can run automatically.
