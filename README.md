# itinerary-prettifier

A Java command-line tool for converting admin-formatted flight itineraries into a clean, customer-friendly format.

---

##  The Problem

"Anywhere Holidays" is a new online travel agency. While their hotel booking system is fully automated, the flight system still involves manual admin work. Admins generate raw, technical itineraries not suited for customers. This tool aims to prettify those itineraries efficiently.

---

##  Features

- Converts airport codes (#IATA and ##ICAO) into readable airport names using a CSV lookup.
- Formats ISO 8601 dates and times:
  - `D(...)` → `05 Apr 2007`
  - `T12(...)` → `12:30PM (-02:00)`
  - `T24(...)` → `12:30 (-02:00)`
- Replaces vertical whitespace characters (`\v`, `\f`, `\r`) with standard line breaks.
- Trims excessive blank lines.
- Adds city name support for airport codes prefixed with `*`.
- Works with non-standard airport-lookup column order.
- Error handling.

---

## Usage

```bash
$ java Prettifier.java ./input.txt ./output.txt ./airports_lookup.csv

---
Help 

$ java Prettifier.java -h
