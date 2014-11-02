# Strategy Table

This is a small project to demonstrate and explore the potential of a design pattern I've been developing that I'm calling Strategy Table.

## Intent
To allow for the operations that act on a hierarchy of 'element' classes to be decoupled from the element classes themselves (as with Visitor) while remaining flexible to changes in both the supported types of operations and the element hierarchy.

## Motivation
In *Design Patterns: Elements of Reusable Object-Oriented Software*, Erich Gamma et al. write that design patterns involve the creation of classes that capture the concept that varies within the architecture of a system.

Strategy Table is meant to act primarily as an augmented version of the Visitor pattern. With Visitor, the aim is to decouple the functionality that can be applied over a hierarchy of 'element' classes from the element classes themselves. The concept that varies in this case is the functionality that can be used with these element classes. Instead of hard-coding new functionality into the element classes, a Visitor class is created that can 'visit' each element within a collection and perform some behaviour based on the runtime type of the element. An element only has to implement a 'visitor accept' method to support any range of possible functionality and allow for new functionality to be implemented for the element classes in the future without necessitating any change in existing class code.

However, a limitation of Visitor is that it is inflexible to change in the element class hierarchy - if new types of elements are added or existing types are removed, the Visitor interface corresponding to that element class hierarchy will be forced to change and this will mean that every concrete implementation of that interface will be forced to change as well.

In another application I've been developing, I've tried to combine the Decorator and Visitor pattern. In order to support dynamic modifications to the behaviour of elements within my application at runtime, I use Decorator classes that I can apply to them. Decorators of elements implement the same interface as the base elements themselves, and should be treated similarly by a client. However, this means that creating a new kind of decorator will likely involve a need to change the Visitor interface to support operations on these new decorators. One could suggest that decorators could simply make visitors visit the elements they decorate rather than themselves, but this eliminates the possibility of incorporating new functionality via a visitor that treats decorators differently than the elements they wrap.

Consider a very unfortunate but possible situation: you have an application that contains a small element hierarchy, a visitor interface with a separate method to accept an element of each type within the hierarchy, and 100 concrete visitors that implement the visitor interface. Now, say you want to add an element decorator to the element hierarchy - this will entail modifying the visitor interface and every one of those 100 concrete visitor implementations, where perhaps only a few of those concrete visitors should treat the decorated element differently than its wrapped element. This involves writing a relatively large amount of code to achieve little, and violates the Open/closed principle of software design.

## Solution
The Strategy Table allows for the element hierarchy and the supported types of operations on those elements to vary without the need to modify any existing code except for the code that configures the StrategyTable object itself.

### Overview
The principle behind StrategyTable is that it maintains a function of the form `f(OperationType, ElementType) -> Strategy`, that is, maintains a mapping from a type of operation and element to the strategy that will be used to handle element/operation combinations of those types. When a client wishes to apply an operation to a particular element, the strategy used to do that will be chosen based on the runtime type of the element and the operation. Before applying operations this way, the client will need to construct and configure the StrategyTable so that the appropriate strategy is selected for every element/operation combination.

A StrategyTable is constructed with three mandatory parameters and one optional parameter. The three parameters that must be given are:

 - *Base element class set*: the set of classes that correspond to the base element types within the element hierarchy that you wish to support operations for.
 - *Decorated element class set*: the set of classes that correspond to the decorated element types within the element hierarchy that you wish to support operations for.
 - *Operation class set*: the set of classes that correspond to the types of operations you wish to apply to elements within the element hierarchy.
 
You can support particular classes of elements or operations just by including `<<ClassName>>.class` in the sets you pass. The base element class set and the decorated element class set must be disjoint - an element cannot be of both a decorated and undecorated type! StrategyTable will throw an exception if you try to do this.

### Strategy Types
Every strategy that the client creates itself should be parameterised on the type of operation that it is meant to support. These strategies can be registered for use with any type of element that the StrategyTable is configured to support and the type of operation that they are parameterised on if the StrategyTable is configured to support it.

There are four special types of strategies that can work with any type of operation:

 - `UnimplementedStrategy`: throws an exception when executed on any operation or element.
 - `NullStrategy`: performs no behaviour when executed on any operation or element.
 - `SubstituteStrategy`: used only with decorator elements; re-applies the operation using the strategy corresponding to the type of the decorator's wrapped element, but substitutes the decorator for the wrapped element in the operation.
 - `BypassStrategy`: used only with decorator elements; re-applies the operation using the strategy corresponding to the type of the decorator's wrapped element and uses the wrapped element in the operation as well. The decorator is completely "transparent" in this case.

### Strategy Registration
After constructing a StrategyTable, you can configure it by registering strategies for each combination of element type and operation type:
 - `registerOperationStrategy(operationType, elementType, strategy)` specifies `f(operationType, elementType) -> strategy`
 - `registerOperationStrategies(operationType, strategy)` specifies `f(operationType, e) -> Strategy, for all e : elementClassSet`
 - `registerNullOperationStrategy(operationType, elementType)` specifies `f(operationType, elementType) -> NullStrategy`
 - `registerNullOperationStrategies(operationType, elementType)` specifies `f(operationType, e) -> NullStrategy, for all e : elementClassSet`
 - `registerNullElementStrategy(elementType)` specifies `f(o, elementType) -> NullStrategy, for all o : operationClassSet`
 - `registerSubstituteElementStrategy(elementType)` specifies `f(o, elementType) -> SubstituteStrategy, for all o : operationClassSet`
 - `registerBypassElementStrategy(elementType)` specifies `f(o, elementType) -> BypassStrategy, for all o : operationClassSet`

### Strategy Locking
Strategies for operations and elements can be "locked in" to avoid future registration calls from changing them:

 - `setOperationStrategiesLocked(operationType)` can be used to lock or unlock the strategies corresponding to operations of type `operationType`.
 - `setElementStrategiesLocked(elementType)` can be used to lock or unlock the strategies corresponding to elements of type `elementType`.
    
The `isStrategyLocked`, `isOperationLocked` and `isElementLocked` methods can be used to determine if a strategy or set of strategies are locked in or not.

### Table Policy
A StrategyTable can be configured with one of four policies that govern how the StrategyTable behaves when the client has not explicitly registered a strategy for a particular element type/operation type combination:

 - `STRICT`: *base element case* and *decorated element case* both use `UnimplementedStrategy`, forcing the client to explicitly register a strategy for the combination if they wish to use it.
 - `NULL:`: *base element case* and *decorated element case* both use `NullStrategy`, so no action is performed for any combination that is left unspecified.
 - `DEFAULT:` *base element case* uses `NullStrategy` and *decorated element case* uses `SubstituteStrategy`.
 - `BYPASS:` *base element case* uses `NullStrategy` and *decorated element case* uses `BypassStrategy`.
 
By default, a StrategyTable will use a `DEFAULT` strategy, and this is the case if a StrategyTable's optional construction parameter is omitted. The fourth optional parameter allows the client to configure the policy of the StrategyTable. A StrategyTable's policy can only be configured during its construction.

## Demonstration
The code provided allows for a demonstration of this design pattern using an element hierarchy and set of operations given below:

**Base element types**

 - `AddElem`
 - `MultElem`
 
**Decorated element types**
 
 - `IgnoreElementDecorator`
 - `ReverseElementDecorator`
 
**Operation types**

 - `FindTotalOperation`
 - `CountElementOperation`
 
Each type of element (including the decorators) exposes the following interface:

    public interface Element {
        int getValue();
        boolean isDecorated();
        int getDecorationLevel();
        Element asDecorationAtLevel(int decorationLevel);
    }
    
Each element can return a value, declare whether it is decorated, return its level of decoration and return a representation of itself at a certain level of decoration.

`AddElem` and `MultElem` behave identically and the demonstration serves to show how elements of each type can be treated differently by different strategies based on their runtime types alone.

`IgnoreElementDecorator` throws an exception if its `getValue` method is called.

`ReverseElementDecorator` returns the inverted value of its wrapped element.

`FindTotalOperation` is designed to go through a sequence of elements and find the total of their values. The way it does this will be based on both the values the elements return (which decorators can modify) and the strategies it is configured to use within StrategyTable.

`CountElementOperation` is designed to count the number of elements within the sequence of elements it operates over.

When the application is run, you are able to create a collection of elements with arbitrary integer values and applied decorations, choose a particular type of strategy table and execute an instance of each type of operation (`FindTotalOperation` and `CountElementOperation`) using it.