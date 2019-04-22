package core;

import java.util.ArrayList;
import java.util.List;

public class ExtendibleHashing {

    public static int BUCKET_SIZE = 4, MAX_GLOBAL = 6;

    private Map<String, Bucket> table;

    public ExtendibleHashing(int bucketSize) {
        this(bucketSize, MAX_GLOBAL);
    }

    public ExtendibleHashing(int bucketSize, int maxGlobal) {
        table = new BST<>();

        List<String> arr = HashFunctions.getGlobalArray(1);
        for (String prefix : arr) {
            table.insert(prefix, new Bucket(prefix, 1));
        }

        BUCKET_SIZE = bucketSize;
        MAX_GLOBAL = maxGlobal;
    }

    public void insert(Person person) {
        String hash = HashFunctions.hash(person.getKey());
        List<Bucket> buckets = table.toList();

        for (Bucket bucket : buckets) {
            if (hash.startsWith(bucket.getPrefix())) {
                if (!bucket.insert(person)) {
                    split(bucket, person);
                }
                return;
            }
        }
    }

    public void remove(Person person) {
        String hash = HashFunctions.hash(person.getKey());
        List<Bucket> buckets = table.toList();

        for (Bucket bucket : buckets) {
            if (hash.startsWith(bucket.getPrefix())) {
                if (bucket.find(person)) {
                    bucket.delete(person);
                    if (!checkCoalescing(bucket) && bucket.isEmpty()) {
                        emptyBucket(bucket);
                    }
                }
                break;
            }
        }
    }

    public void remove(int key) {
        Person person = find(key);
        if (person != null) {
            remove(person);
        }
    }

    public Person find(int key) {
        String hash = HashFunctions.hash(key);
        List<Bucket> buckets = table.toList();

        for (Bucket bucket : buckets) {
            if (hash.startsWith(bucket.getPrefix())) {
                List<Person> people = bucket.getPeople();

                for (Person person : people) {
                    if (person.getKey() == key) {
                        return person;
                    }
                }
            }
        }

        return null;
    }

    public Bucket getBucket(int key) {
        if (find(key) == null) {
            return null;
        }

        String hash = HashFunctions.hash(key);
        List<Bucket> buckets = table.toList();

        for (Bucket bucket : buckets) {
            if (hash.startsWith(bucket.getPrefix())) {
                return bucket;
            }
        }

        return null;
    }

    public void split(Bucket bucket, Person person) {
        if (bucket.getLocal() == MAX_GLOBAL) {
            overflow(bucket, person);
            return;
        }

        Bucket b1 = new Bucket(bucket.getPrefix() + "0", bucket.getLocal() + 1);
        boolean b1Split = false;
        Bucket b2 = new Bucket(bucket.getPrefix() + "1", bucket.getLocal() + 1);
        boolean b2Split = false;
        List<Person> people = bucket.getPeople();
        people.add(person);

        for (Person p : people) {
            if (HashFunctions.hash(p.getKey()).startsWith(b1.getPrefix())) {
                if (!b1.insert(p)) {
                    split(b1, p);
                    b1Split = true;
                }
            } else {
                if (!b2.insert(p)) {
                    split(b2, p);
                    b2Split = true;
                }
            }
        }

        table.remove(bucket.getPrefix());
        if (!b1Split && b1.getSize() > 0) {
            table.insert(b1.getPrefix(), b1);
        }
        if (!b2Split && b2.getSize() > 0) {
            table.insert(b2.getPrefix(), b2);
        }
    }

    public void printTable() {
        List<Bucket> buckets = table.toList();

        for (int i = 0; i < buckets.size(); i++) {
            System.out.print("Bucket #" + (i+1) + " L(" + buckets.get(i).getLocal() + "):");
            List<Person> people = buckets.get(i).getPeople();
            for (Person person : people) {
                System.out.print(" " + person.getKey() + " -");
            }
            System.out.println();
        }
    }

    public List<String> getBucket(String address) {
        List<String> arr = new ArrayList<>();
        List<Bucket> buckets = table.toList();

        for (Bucket bucket : buckets) {
            if (address.startsWith(bucket.getPrefix())) {
                List<Person> people = bucket.getPeople();

                for (Person person : people) {
                    arr.add(person.toString());
                }

                break;
            }
        }

        return arr;
    }

    public int getBucketLocal(String address) {
        List<String> arr = new ArrayList<>();
        List<Bucket> buckets = table.toList();

        for (Bucket bucket : buckets) {
            if (address.startsWith(bucket.getPrefix())) {
                return bucket.getLocal();
            }
        }

        return 1;
    }

    private void overflow(Bucket bucket, Person person) {
        bucket.setOverflow(true);
        bucket.insert(person);
    }

    private void emptyBucket(Bucket bucket) {
        List<Bucket> buckets = table.toList();
        if (bucket.getLocal() > 1 && buckets.size() > 2) {
            table.remove(bucket.getPrefix());
        }
    }

    private boolean checkCoalescing(Bucket bucket) {
        if (bucket.getLocal() == 1) {
            return false;
        }

        List<Bucket> buckets = table.toList();
        String hash = bucket.getPrefix();

        for (Bucket b : buckets) {
            if (bucket == b) {
                continue;
            } else if (hash.startsWith(b.getPrefix().substring(0, b.getLocal() - 1))
                    && bucket.getSize() + b.getSize() <= BUCKET_SIZE) {
                combine(bucket, b);
                return true;
            }
        }

        return false;
    }


    private void combine(Bucket b1, Bucket b2) {
        Bucket bucket = new Bucket(b1.getPrefix().substring(0, b1.getLocal() - 1), b1.getLocal() - 1);
        List<Person> people = b1.getPeople();
        people.addAll(b2.getPeople());

        for (Person person : people) {
            bucket.insert(person);
        }

        table.remove(b1.getPrefix());
        table.remove(b2.getPrefix());
        table.insert(bucket.getPrefix(), bucket);
    }

    public Map<String, Bucket> getTable() {
        return table;
    }

    public void setTable(Map<String, Bucket> table) {
        this.table = table;
    }

    public int getGlobal() {
        List<Bucket> buckets = table.toList();
        int global = 1;

        for (Bucket bucket : buckets) {
            if (bucket.getLocal() > global) {
                global = bucket.getLocal();
            }
        }

        return global;
    }

}
