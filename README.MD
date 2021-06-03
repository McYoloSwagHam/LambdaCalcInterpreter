# LambdaCalcInterpreter
This is an interpreter for lambda calculus, it takes userinput and simplifies & evaluates it as best it can, it can also show the AST as it reduces it, this was made for my USI PF2 2021 project

## Building & Compilation & Usage

```
javac *.java && java main 
```
or if you want very VERBOSE AST PRINTING & FORMATTING
```
javac *.java && java main -v
```
-v will print the AST at every step and the NODE being reduced.

To use this just paste in a lambda calc expression or type it in, look at some of the examples below.

or in BlueJ, Project -> Create Jar File... use main as the main class.

or  with maven
```
mvn package
```
then to run the jar
```
java -jar LambdaCalc... .jar -V
```

## Fun lambda expressions with church numerals

```
(\f.\x.(f (f x)))(\a.\b.\c.(b (a b c)))(\f.\x.(f (f x))) = 2 + 2 = 2 SUCC 2
(\a.\b.\f.(a (b f)))(\f.\x.(f (f x)))(\f.\x.(f (f (f x)))) = 2 * 3 = MULT 2 3
(\n.\f.\x.(n (\g.\h.(h (g f))) (\u.(x)) (\u.(u)) ))(\e.\l.(e (e (e l)))) = PRED 3 // in this case 
(\b.\e.(e b))(\f.\x.(f (f (f x))))(\f.\x.(f (f d))) = EXP 3 2 =  3^2 // note that the church numerals have to have different bases, one is f\'x the other f\'x
```

AUTHOR: Ayaz Mammadov
