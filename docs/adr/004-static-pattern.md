Title: 004 - Static Utility Classes Pattern

Status: Accepted

## Context

We needed to decide how to structure utility and service classes in the codebase. The question was: should classes like Input, Actions, ConsoleDisplay, and MenuHandler be instance classes or static classes?

## Decision

Use static classes for utility and service classes that:
- Perform operations but hold no instance state
- Are named after what they do (actions, handling, display)

Use instance classes for model/domain entities that:
- Hold data representing real-world things
- Are named after what they are (Student, Teacher, ClassRoom)

## Rationale

Static utility classes (like Java's Arrays, Collections, Scanner) are appropriate when:
- The class has no data fields that vary between instances
- All methods can be static
- The class represents a "tool" that does things, not a "thing" that has data

Instance classes (models) are appropriate when:
- The class represents a data entity with multiple properties
- You need multiple instances with different data
- The class encapsulates state

Examples from this project:

| Class | Type | Static? |
|-------|------|---------|
| Student | Model (data) | No |
| Teacher | Model (data) | No |
| ClassRoom | Model (data) | No |
| Input | Utility (reads input) | Yes |
| Actions | Service (business logic) | Yes |
| ConsoleDisplay | Service (display output) | Yes |
| MenuHandler | Service (UI handling) | Yes |

## Consequences

Positive:
- Cleaner API - call Input.getStrInput() without instantiation
- No unnecessary object creation
- Follows Java standard library conventions
- Clear separation between "things" (models) and "tools" (utilities)

Negative:
- Can't subclass or mock for testing (but can with interfaces if needed)
- Less flexible if state needs to be added later

## Implementation notes

To convert a class to static:
1. Make all methods `static`
2. Move any instance fields to static if truly needed, or remove
3. Update all callers to use ClassName.method() instead of instance.method()
4. Remove instantiation (new ClassName())

## References

- Java standard library uses this pattern (Arrays, Collections, Math, Scanner)
- Related: docs/learnings/static-pattern.md (learning notes)
