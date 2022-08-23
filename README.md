
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

```text
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
```json
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
- `/append "[line name]" "[station name]"`: appends a station to the end of 
  the specified line
- `/add-head "[line name]" "[station name]"`: adds a station to the 
  beginning of the specified line
- `/remove "[line name]" "[station name]"`: removes the specified station 
  from the line
- `/output "[line name]"`: print the specified line
- `/exit`: exit the program

### Stage 3: [A real metro](https://hyperskill.org/projects/120/stages/650/implement)

#### _Connect the individual lines into a complex system._

Our metro is growing, and now it is time to combine the different lines into a
single underground system. In places where the lines connect, there are 
large interchange stations where people can cross from one line to another.
Our program should store such interchange stations and which line they 
connect to.

In order to store this kind of data, we need to add a new field to the 
item class which is a pointer to another station (to store several 
stations, it should be an array). This upgrade will allow us to connect 
the lines and make the metro a more advanced system.

- Add the ability to read transfer stations from a JSON file:
```json
{
    "Metro-Railway": {
        "3": {
            "name": "Baker street",
            "transfer": [{
                "line": "Hammersmith-and-City",
                "station": "Baker street"
            }]
        },
        "1": {
            "name": "Bishops-road",
            "transfer": []
        },
        "2": {
            "name": "Edgver road",
            "transfer": []
        }
    },
    "Hammersmith-and-City": {
        "2": {
            "name": "Westbourne-park",
            "transfer": []
        },
        "1": {
            "name": "Hammersmith",
            "transfer": []
        },
        "3": {
            "name": "Baker street",
            "transfer": [{
                "line": "Metro-Railway",
                "station": "Baker street"
            }]
        }
    }
}
```
- Connect the stations using the command `/connect "[line 1]" "[station 1]" 
  "[line 2]" "[station 2]"`
- The program should print the name of the connected station:
```text
> /output Hammersmith-and-City
depot
Hammersmith
Westbourne-park
Baker-street - Baker-street (Metro-Railway line)
depot
```

### Stage 4: [The shortest route](https://hyperskill.org/projects/120/stages/651/implement)

#### _Add the ability to find the shortest route from the passenger's location to their destination._

Now that our metro has gotten big, the passengers are finding it difficult 
to find their way around it. Your task is to create an application that 
can pave the route from one station to another, displaying all the 
necessary stations and transitions. The application must also find the 
path from one point to another so that people don't have to be stuck 
underground for too long.

In the previous step, our data structure looked like a Graph, and we need 
to use a special algorithm to find the shortest (the smallest number of 
stations) way from one point to another. In this case, let's use the 
**Breadth-First search algorithm**, which is really common and quite easy 
to understand.

- Add a feature to search for a path with the command `/route "[line 1]" 
  "[station 1]" "[line 2]" "[station 2]"`
- The program should print every passed station and every transition:
```text
> /route Metro-Railway "Edgver road" Hammersmith-and-City Westbourne-park
Edgver road
Baker street
Transition to line Hammersmith-and-City
Baker street
Westbourne-park
```

### Stage 5: [The fastest route](https://hyperskill.org/projects/120/stages/652/implement)

#### _Using a weighted graph, add the ability to calculate the estimated travel time._

Good job: now the passengers can easily find their way around the metro! 
However, helping them plan their travel time requires sme more effort. We 
did not take into account one important detail: the distances between the 
stations vary, which means that the travel time varies as well. we need to 
find not just the shortest path, but the fastest one. People who are late 
for work will thank you! you have all the necessary information about the 
distance between the stations: pay attention to the example!

The kind of graph we need here is called a **weighted graph** because its 
edges have "weight", or, in other words, value. To solve this specific 
problem, the algorithm from the previous stage cannot be used. Dijkstra's 
algorithm, on the other hand, is a great choice for this task! It is also 
a common algorithm for finding the shortest path, but it takes into 
account the weight of the edges.

- Add the ability to find the fastest way using the command 
  `/fastest-route "[line 1]" "[station 1]" "[line 2]" "[station 2]"`.
- Upgrade the `/add-head` and `/append` station commands by adding travel 
  time.
- Take it into account that transferring from one line to another takes 5 
  minutes.
- The program should print the estimated total travel time.
```text
> /fastest-route Hammersmith-and-City "Baker street" Hammersmith-and-City Hammersmith
Baker street
Westbourne-park
Hammersmith
Total: 4 minutes in the way
```

### Stage 6: [Branching](https://hyperskill.org/projects/120/stages/653/implement)

#### _Update your program to fit any kind of metro system, even the most complex ones._

Your program can help effectively navigate the metro; at least, if the 
metro lines have one beginning and one end. However, not all metro systems 
are like that: take a look at the London Underground, for example. Its 
metro lines are divided into two or even three looped sections. This 
system is difficult for a person to understand, let alone a computer program.

There's a solution: let's change the presentation of the information. 
Each station will now have the fields "next", "past", and "transfer" that 
contain arrays because these fields can hold multiple objects.

- Add branching.
- Add the possibility of a looped line.
- Line output is not checked in this stage, but you can work on it on your 
  own.
