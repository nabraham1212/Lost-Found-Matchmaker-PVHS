# Lost & Found Matchmaker

A Java console-based school lost-and-found system that stores reports and ranks possible matches using item details like type, color, brand, location, and date.

## Overview
This project helps organize lost and found reports at school by allowing users to:
- report a lost item
- report a found item
- find ranked matches for a lost item
- view all lost reports
- view all found reports
- search lost items by type

The program uses a weighted scoring system to compare reports by:
- item type
- color
- brand
- location
- date

It also saves reports permanently using text files, so data remains after the program closes.

## Features
- Console-based menu
- Input validation
- Ranked match results
- Search by item type
- File I/O persistence
- Built-in sample data on first run

## Object-Oriented Design
This project uses:
- **Abstraction** through the `Item` abstract class
- **Inheritance** through `LostItem` and `FoundItem`
- **Polymorphism** through overloaded methods and overridden methods
- **Encapsulation** through private fields and getters

## Files
- `Main.java` - runs the menu and handles user input
- `Item.java` - abstract parent class for shared item details
- `LostItem.java` - stores lost item reports
- `FoundItem.java` - stores found item reports
- `Matchmaker.java` - handles saving, loading, searching, and matching
- `MatchResult.java` - stores one scored match result
- `TestRunner.java` - runs organized tests for the program

## How to Run
1. Open the project in IntelliJ or VS Code
2. Compile all `.java` files
3. Run `Main.java`

## Testing
The program was tested for:
- valid and invalid menu input
- blank field rejection
- PVHS email / school ID validation
- date validation
- file saving and loading
- ranked matching results
- search by item type

Run `TestRunner.java` to view test results.

## Author
Nevin Abraham & Aarush Behera
