package uk.co.shikanga.data;


public class Fruit {

    private Long id;
    private String name;

    public Fruit() {
    }

    public Fruit(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

}
