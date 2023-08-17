package com.inn.cafe.repository;

import com.inn.cafe.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Integer> {

    /**En résumé, cette requête renvoie toutes les catégories qui ont au moins un produit dont le statut est "true".**/
    @Query("SELECT c FROM Category c WHERE c.id IN (SELECT p.category FROM Product p WHERE p.status = 'true')")
    List<Category> getAllCategory();
}
