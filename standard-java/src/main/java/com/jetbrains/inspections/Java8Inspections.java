package com.jetbrains.inspections;

import com.jetbrains.inspections.entities.Converter;
import com.jetbrains.inspections.entities.Counter;
import com.jetbrains.inspections.entities.Person;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.lang.System.out;
import static java.util.Arrays.sort;
import static java.util.stream.Collectors.toList;

/** @noinspection rawtypes*/
@SuppressWarnings({"unused", "unchecked"})
public class Java8Inspections {
    private final Map<Integer, List<String>> integerStringMap = new HashMap<>();

    private final String[] stringArray = new String[]{"IntelliJ IDEA", "AppCode", "CLion", "Upsource"};
    private List<Converter> converters;

    public void lambdas() {
        //Anonymous function Function<Function, Function>() can be replaced with lambda
        Function<Function, Function> f1 = function -> function.compose(function);

        //Remove redundant types
        Function<Function, Function> f3 = (Function function) -> function.compose(function);

        //Expand lambda expression body to {...}
        Function<Function, Function> f2 = function -> function.compose(function);

        //Statement lambda can be replaced with expression lambda
        Function<Function, Function> f4 = (Function function) -> function.compose(function);

        //Lambda can be replaced with method reference
        sort(stringArray, String::compareToIgnoreCase);

        //Replace with forEach on foo
        ArrayList<String> foo = getStrings();
        for (String s : foo) {
            if (s != null) {
                out.println(s);
            }
        }
    }

    private int replaceWithCountSimple() {
        int count = (int) Arrays.stream(stringArray)
                                .count();

        return count;
    }

    private int replaceWithCountNested() {
        int count = integerStringMap.values()
                                    .stream()
                                    .filter(Objects::nonNull)
                                    .mapToInt(list -> (int) list.stream()
                                                                .filter(stringVal -> stringVal
                                                                        .contains("error"))
                                                                .count())
                                    .sum();

        return count;
    }

    private int replaceWithSum() {
        int count = Arrays.stream(stringArray)
                          .mapToInt(String::length)
                          .sum();

        return count;
    }

    private int replaceWithMapToInt() {
        int count = integerStringMap.values()
                                    .stream()
                                    .filter(Objects::nonNull)
                                    .mapToInt(list -> list.stream()
                                                          .filter(stringVal -> stringVal.contains("error"))
                                                          .mapToInt(String::length)
                                                          .sum())
                                    .sum();

        return count;
    }

    private List<String> replaceWithCollect() {
        List<String> result = getStrings();

        Arrays.stream(stringArray)
              .filter(Objects::nonNull)
              .flatMap(line -> Arrays.stream(line.split("\\s")))
              .forEach(result::add);
        return result;
    }

    private void replaceMapForEachWithEntrySetLoop(Map<String, Integer> map) {
        map.forEach((k, v) -> {
            if (k.isEmpty()) return;
            System.out.println("Key: " + k + "; value: " + v);
        });
    }

    private void replaceMapForEachWithEntrySetLoop(Map<String, Integer> map, Map<String, Integer> otherMap) {
        map.forEach(otherMap::putIfAbsent);
    }

    //<editor-fold desc="Helper methods">

    private List<String> replaceWithCollectAndMap() {
        List<String> result = getStrings();

        Arrays.stream(stringArray)
              .filter(Objects::nonNull)
              .flatMap(line -> Arrays.stream(line.split(" ")))
              .map(word -> word.substring(0, 3))
              .forEach(result::add);
        return result;
    }

    private List<String> getListOfAllNonEmptyStringValues(Map<String, List<String>> map) {
        List<String> result = getStrings();
        map.entrySet()
           .stream()
           .filter(entry -> !entry.getKey().isEmpty())
           .map(Map.Entry::getValue)
           .filter(Objects::nonNull)
           .flatMap(Collection::stream)
           .map(String::trim)
           .filter(trimmed -> !trimmed.isEmpty())
           .forEach(result::add);
        return result;
    }

    private boolean hasEmptyString(String[][] data) {
        return Arrays.stream(data)
                     .flatMap(Arrays::stream)
                     .anyMatch(String::isEmpty);
    }

    private void incrementCounterForId(Map<Integer, Counter> idToCounter, Integer id) {
        Counter counter = idToCounter.get(id);
        if (counter == null) {
            counter = new Counter();
            idToCounter.put(id, counter);
        }
        counter.incrementCount();
    }

    private Counter getCounterForId(Map<Integer, Counter> idToCounter, Integer id) {
        Counter counter = idToCounter.get(id);
        if (counter == null) {
            counter = Counter.EMPTY;
        }
        return counter;
    }

    private void removeIfCountExceedsLimit(Collection<Counter> counters) {
        Predicate<Counter> predicate = (c) -> c.getCount() > 100;

        counters.removeIf(predicate::test);
    }

    private List<Counter> findTopTenAlt(List<Counter> counters) {
        return counters.stream()
                       .sorted(Comparator.comparingInt(Counter::getCount))
                       .limit(10)
                       .collect(toList());

    }

    private List<Counter> findTopTen(List<Counter> counters) {
        return counters.stream()
                       .sorted(Counter::compareTo)
                       .limit(10)
                       .collect(toList());

    }

    //Streams: findFirst
    private Converter getFirstConverterForClass(final Class aClass) {
        return converters.stream()
                         .filter(converter -> converter.canHandle(aClass))
                         .findFirst()
                         .orElse(Converter.IDENTITY_CONVERTER);
    }

    //Arrays: findFirst
    public String toCountedLoopInFindFirst(int[] data, List<String> info) {
        return Arrays.stream(data)
                     .flatMap(val -> IntStream.rangeClosed(0, val))
                     .mapToObj(info::get)
                     .filter(str -> !str.isEmpty())
                     .findFirst()
                     .orElse(null);
    }

    //Streams: toArray
    public String[] replaceWithToArray(List<String> data) {
        List<String> result = getStrings();
        data.stream()
            .filter(str -> !str.isEmpty())
            .forEach(result::add);
        return result.toArray(new String[0]);
    }

    //Streams: sorting
    public List<String> getSortedListOfNames(List<Person> persons) {
        List<String> names = getStrings();
        persons.stream()
               .map(Person::getName)
               .forEach(names::add);
        names.sort(String::compareToIgnoreCase);
        return names;
    }

    public long countNumberOfItems(List<String> strings) {
        return strings.stream()
                      .count();
    }

    //2017.3
    public String[] fuseStepsIntoStream() {
        final Stream<String> stream = Stream.of("a", "b", "c");

        final List<String> strings = stream.collect(Collectors.toList());
        strings.sort(Comparator.naturalOrder());

        return strings.toArray(new String[0]);
    }

    public Stream<Object> simplifyStreamAPICallChain1() {
        return Collections.nCopies(10, "")
                          .stream()
                          .map(s -> doMapping());
    }

    public boolean simplifyStreamAPICallChain2() {
        final Stream<String> stream = Stream.of("a", "b", "c");
        return stream.filter(this::stringMatchesSomeCriteria)
                     .count() > 0;
    }

    public Stream<Object> simplifyStreamAPICallChain3(Object[] array) {
        return IntStream.range(1, 10)
                        .mapToObj(value -> array[value]);
    }

    public String useJoiningForStringBuilders(Character[] value) {
        final String builder = Arrays.stream(value)
                                     .map(String::valueOf)
                                     .collect(Collectors.joining());
        return builder;
    }

    public String collapseBuilderIntoStreamOperation(CustomError[] ve) {
        final String sb = Arrays.stream(ve)
                                .map(CustomError::render)
                                .collect(Collectors.joining("", "Number of violations: " + ve.length + " \n", ""));
        return sb;
    }

    public MappedField smarterStreamInspections(final String storedName, List<MappedField> persistenceFields) {
        return persistenceFields.stream()
                                .filter(mf -> Arrays.stream(mf.getLoadNames())
                                                    .anyMatch(storedName::equals))
                                .findFirst()
                                .orElse(null);
    }

    public boolean smarterStreamInspections2(List<Map<String, String>> indexInfo) {
        boolean indexFound = indexInfo.stream()
                                      .map(item -> "nested.field.fail".equals((item.get("key"))))
                                      .reduce(false, (a, b) -> a || b);

        return indexFound;
    }

    public void simplifyMatchOperations(List<String> list) {
        if (!list.isEmpty()) {
            return;
        }

        final boolean hasNoNulls = list.stream()
                                       .allMatch(Objects::nonNull);
        doSomething(hasNoNulls);

        final Optional<Object> first = Stream.empty()
                                             .findFirst();
        doSomething(first.isPresent());

        final Optional<Object> any = Stream.empty()
                                           .findAny();
        doSomething(any.isPresent());

        //min, max, reduce
        final Optional<Object> min = Stream.empty()
                                           .min(Comparator.comparing(Object::toString));
        doSomething(min.isPresent());

        //sum & count
        final long count = IntStream.empty()
                                    .count();
        doSomething(count);

        final int sum = IntStream.empty()
                                 .sum();
        doSomething(sum);
    }

    public void nullabilityAnalysis(String[] stringArray) {
        Arrays.stream(stringArray)
              .map(s -> s.isEmpty() ? s : null)
              .map(String::trim)
              .collect(Collectors.toList());
    }

    public Optional<String> identifyUnnecessarySortCalls(Stream<String> strings) {
        return strings.filter(Objects::nonNull)
                      .sorted(Comparator.comparing(String::length))
                      .min(Comparator.comparing(String::length));
    }

    public Optional<String> identifyReveredMaxMin(Stream<String> strings) {
        return strings.filter(Objects::nonNull)
                      .min(Comparator.comparing(String::length)
                                     .reversed());
    }

    @NotNull
    public String[] nullabilityAnalysisForStreamChains() {
        return Stream.of("a")
                     .map(s -> s.matches("\\w") ? s.toLowerCase() : null)
                     .toArray(String[]::new);
    }

    public String suggestCollectorsJoining() {
        return String.join(",",
                IntStream.range(0, 10)
                         .mapToObj(String::valueOf)
                         .collect(toList())
        );
    }

    public void simplificationsForEntrySet(Map<Integer, String> map) {
        Stream<Integer> integerStream = map.entrySet()
                                           .stream()
                                           .map(Map.Entry::getKey);
    }


    @NotNull
    private ArrayList<String> getStrings() {
        return new ArrayList<>();
    }

    private void doSomething(boolean hasNoNulls) {

    }

    private void doSomething(long count) {

    }

    private <R> R doMapping() {
        return null;
    }

    private boolean stringMatchesSomeCriteria(String s) {
        return false;
    }

    private class CustomError {
        public String render() {
            return null;
        }
    }

    private class MappedField {
        private String[] loadNames;

        public String[] getLoadNames() {
            return loadNames;
        }
    }

    //</editor-fold>
}

























