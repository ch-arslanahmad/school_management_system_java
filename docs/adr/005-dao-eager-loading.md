# ADR-005: How DAOs load/handle class data

Date: 2026-04-21
Status: Accepted

## Problem

Our domain classes (Subjects, Student) had a reference to a ClassRoom object. This made the model tied to another class and created potential problems (overnesting & circular dependencies) if we wanted to return this data from an API.

## What we tried

Lazy way: DAOs return only classID (a number). If UI needs the full class details, code elsewhere fetches it.

Eager way: DAOs run a SQL JOIN and return (classID, className) together.

## What we chose

Eager, DAOs use JOINs to return classID and className directly.

Why:

- Keeps the domain model simple, just stores the ID and name, not a whole ClassRoom object.
- No deep object chains, which means no circular dependency issues.

## Changes made

- Subjects now stores `classID` and `className`, no ClassRoom reference in any way.
- Student stores ClassRoom internally but DAO sets `classID`/`className` directly.
- Removed `getClassRoom()` from Subjects since we don't need it.
- If code needs full ClassRoom (like fees), it calls ClassDAO.fetchClass() from the service layer.