package core;

import java.util.ArrayList;
import java.util.List;

public class Bucket {

    private Bucket nextBucket;
    private List<Person> people;
    private String prefix;
    private boolean isOverflow;
    private int local, nbPeople;

    public Bucket(String prefix, int local) {
        this.prefix = prefix;
        this.local = local;
        nextBucket = null;
        people = new ArrayList<>();
        isOverflow = false;
        nbPeople = 0;
    }

    public boolean insert(Person person) {
        if (nbPeople == ExtendibleHashing.BUCKET_SIZE) {
            if (isOverflow) {
                nextBucket = new Bucket(prefix, local);
                nextBucket.setOverflow(true);
            }
            return isOverflow && nextBucket.insert(person);
        }

        people.add(person);
        nbPeople++;
        return true;
    }

    public boolean find(Person person) {
        return getPeople().contains(person);
    }

    public void delete(Person person) {
        Bucket bucket = this;
        while (true) {
            if (bucket.people.contains(person)) {
                bucket.people.remove(person);
            }

            if (bucket.getNextBucket() == null) {
                break;
            }
        }
    }

    public List<Person> getPeople() {
        List<Person> people = new ArrayList<>();
        people.addAll(this.people);

        if (nextBucket != null) {
            people.addAll(nextBucket.getPeople());
        }

        return people;
    }

    public int getSize() {
        return getPeople().size();
    }

    public boolean isEmpty() {
        return getPeople().size() == 0;
    }

    public void setPeople(List<Person> people) {
        this.people = people;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public int getLocal() {
        return local;
    }

    public void setLocal(int local) {
        this.local = local;
    }

    public Bucket getNextBucket() {
        return nextBucket;
    }

    public void setNextBucket(Bucket nextBucket) {
        this.nextBucket = nextBucket;
    }

    public boolean isOverflow() {
        return isOverflow;
    }

    public void setOverflow(boolean overflow) {
        isOverflow = overflow;
    }

    public List<String> toStringList() {
        List<Person> people = getPeople();
        List<String> arr = new ArrayList<>();

        for (Person person : people) {
            arr.add(person.toString());
        }

        return arr;
    }

}
