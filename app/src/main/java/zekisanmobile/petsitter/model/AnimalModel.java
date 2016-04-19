package zekisanmobile.petsitter.model;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.realm.Realm;
import io.realm.RealmResults;
import zekisanmobile.petsitter.di.component.AppComponent;
import zekisanmobile.petsitter.util.ValidationUtil;
import zekisanmobile.petsitter.vo.Animal;

public class AnimalModel {

    @Inject
    Realm realm;

    public AnimalModel(AppComponent appComponent) {
        appComponent.inject(this);
    }

    public void save(Animal animal){
        animal.validate();
        realm.beginTransaction();
        realm.copyToRealm(animal);
        realm.commitTransaction();
    }

    public void saveAll(final List<Animal> animals){
        ValidationUtil.pruneInvalid(animals);
        if (animals.isEmpty()) {
            return;
        }
        realm.beginTransaction();
        for (Animal animal : animals) {
            realm.copyToRealm(animal);
        }
        realm.commitTransaction();
    }

    public Animal find(long id) {
        return realm.where(Animal.class).equalTo("id", id).findFirst();
    }

    public List<Animal> all(){
        RealmResults<Animal> animalsFromDb = realm.where(Animal.class).findAll();
        List<Animal> animals = new ArrayList<>();
        for (Animal animal : animalsFromDb) {
            animals.add(animal);
        }
        return animals;
    }

    public Animal findByName(String name) {
        return realm.where(Animal.class).equalTo("name", name).findFirst();
    }
}