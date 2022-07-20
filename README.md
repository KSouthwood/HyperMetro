
# [HyperMetro](https://hyperskill.org/projects/120)

## About

Have you ever been worried about getting lost in the subway? Do you need to
find a faster route to your destination? Write an application that will help
you better navigate the complicated metro system.

## Learning Outcomes

You will gain a better understanding of algorithms and data structures such
as doubly linked list and graphs.

### Stage 1: [One-line metro](https://hyperskill.org/projects/120/stages/648/implement)

#### _With the help of a singly-linked list, make a representation of a simple one-line metro system._

Imagine you work for the metro system and your task is to develop a program 
that displays all the stations of the current line. At this point, let's 
assume that the metro has only one line.

You have a simple text file with the information about the names of the 
stations. The path to the file is specified by the command line argument. 
your program should read the file, generate a singly-linked list of the 
stations, and print them like this:

```
depot - Station 1 - Station 2
Station 1 - Station 2 - Station 3
Station 2 - Station 3 - Station 4
...
Station (n-1) - Station n - depot
```



### Stage 2: [Several directions](https://hyperskill.org/projects/120/stages/649/implement)

#### _Each line goes both ways: reflect two directions in your program and add a second metro line._

It's time to expand our metro! To provide the citizens with more efficient
transportation, we will extend our existing metro line and start a new one.
Since we will now have several lines, there should be a possibility to choose
which one to use. Also, let's not forget that trains go both directions (
except for the end stations).

Since the file stores not just one metro line but two, it is easier to 
organize the data by splitting it up between the two lines. In this case, 
we use a JSON file. Add new stations in the given order:
```
{
    "line 1": {
        "3": "station3",
        "1": "station 1",
        "2": "station 2"
    },
    "line 2": {
        "2": "station 2",
        "1": "station 1"
    }
}
```

Add the following commands to the program:
- /append "[line name]" "[station name]": appends a station to the end of 
  the specified line
- /add-head "[line name]" "[station name]": adds a station to the 
  beginning of the specified line
- /remove "[line name]" "[station name]": removes the specified station 
  from the line
- /output "[line name]": print the specified line
- /exit: exit the program

### Stage 3: [A real metro](https://hyperskill.org/projects/120/stages/650/implement)

#### _Summary_

Text

### Stage 4: [The shortest route](https://hyperskill.org/projects/120/stages/651/implement)

#### _Add the ability to find the shortest route from the passenger's location to their destination._

Text

### Stage 5: [The fastest route](https://hyperskill.org/projects/120/stages/652/implement)

#### _Using a weighted graph, add the ability to calculate the estimated travel time._

Text

### Stage 6: [Branching](https://hyperskill.org/projects/120/stages/653/implement)

#### _Update your program to fit any kind of metro system, even the most complex ones._

Text
