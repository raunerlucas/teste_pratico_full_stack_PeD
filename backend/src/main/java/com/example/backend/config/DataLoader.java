package com.example.backend.config;

import com.example.backend.entity.Product;
import com.example.backend.entity.ProductComposition;
import com.example.backend.entity.RawMaterial;
import com.example.backend.repository.ProductRepository;
import com.example.backend.repository.RawMaterialRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * Componente responsável por popular o banco de dados com dados iniciais
 * na primeira execução da aplicação.
 *
 * <p>Utiliza {@link CommandLineRunner} para executar a carga logo após o contexto
 * do Spring ser inicializado. A inserção só ocorre se o banco estiver vazio
 * (verifica se já existem matérias-primas cadastradas), garantindo idempotência.</p>
 *
 * <h3>Dados carregados</h3>
 * <ul>
 *   <li><strong>5 Matérias-Primas:</strong> Farinha de Trigo, Açúcar, Leite, Ovos, Manteiga</li>
 *   <li><strong>3 Produtos:</strong> Pão Francês, Bolo de Chocolate, Biscoito Amanteigado</li>
 *   <li>Cada produto possui composições vinculando matérias-primas com quantidades necessárias.</li>
 * </ul>
 *
 * @author Equipe Backend
 * @version 1.0.0
 */
@Component
public class DataLoader implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataLoader.class);

    private final RawMaterialRepository rawMaterialRepository;
    private final ProductRepository productRepository;

    public DataLoader(RawMaterialRepository rawMaterialRepository,
                      ProductRepository productRepository) {
        this.rawMaterialRepository = rawMaterialRepository;
        this.productRepository = productRepository;
    }

    @Override
    public void run(String... args) {
        if (rawMaterialRepository.count() > 0) {
            log.info("✅ Banco de dados já possui dados. Carga inicial ignorada.");
            return;
        }

        log.info("📦 Banco vazio detectado. Iniciando carga de dados iniciais...");

        // =====================================================================
        // 1. Matérias-Primas
        // =====================================================================
        RawMaterial farinha = rawMaterialRepository.save(
                RawMaterial.builder()
                        .code("MP001")
                        .name("Farinha de Trigo")
                        .stockQuantity(1000.0)
                        .build()
        );

        RawMaterial acucar = rawMaterialRepository.save(
                RawMaterial.builder()
                        .code("MP002")
                        .name("Açúcar")
                        .stockQuantity(500.0)
                        .build()
        );

        RawMaterial leite = rawMaterialRepository.save(
                RawMaterial.builder()
                        .code("MP003")
                        .name("Leite")
                        .stockQuantity(300.0)
                        .build()
        );

        RawMaterial ovos = rawMaterialRepository.save(
                RawMaterial.builder()
                        .code("MP004")
                        .name("Ovos")
                        .stockQuantity(200.0)
                        .build()
        );

        RawMaterial manteiga = rawMaterialRepository.save(
                RawMaterial.builder()
                        .code("MP005")
                        .name("Manteiga")
                        .stockQuantity(150.0)
                        .build()
        );

        log.info("   ✔ 5 matérias-primas cadastradas.");

        // =====================================================================
        // 2. Produto: Pão Francês (R$ 12.50)
        //    - 200g Farinha, 50ml Leite, 10g Manteiga
        // =====================================================================
        Product pao = Product.builder()
                .code("PRD001")
                .name("Pão Francês")
                .price(12.50)
                .build();

        pao.getCompositions().add(ProductComposition.builder()
                .product(pao).rawMaterial(farinha).requiredQuantity(200.0).build());
        pao.getCompositions().add(ProductComposition.builder()
                .product(pao).rawMaterial(leite).requiredQuantity(50.0).build());
        pao.getCompositions().add(ProductComposition.builder()
                .product(pao).rawMaterial(manteiga).requiredQuantity(10.0).build());

        productRepository.save(pao);

        // =====================================================================
        // 3. Produto: Bolo de Chocolate (R$ 35.00)
        //    - 300g Farinha, 200g Açúcar, 100ml Leite, 50g Ovos, 80g Manteiga
        // =====================================================================
        Product bolo = Product.builder()
                .code("PRD002")
                .name("Bolo de Chocolate")
                .price(35.00)
                .build();

        bolo.getCompositions().add(ProductComposition.builder()
                .product(bolo).rawMaterial(farinha).requiredQuantity(300.0).build());
        bolo.getCompositions().add(ProductComposition.builder()
                .product(bolo).rawMaterial(acucar).requiredQuantity(200.0).build());
        bolo.getCompositions().add(ProductComposition.builder()
                .product(bolo).rawMaterial(leite).requiredQuantity(100.0).build());
        bolo.getCompositions().add(ProductComposition.builder()
                .product(bolo).rawMaterial(ovos).requiredQuantity(50.0).build());
        bolo.getCompositions().add(ProductComposition.builder()
                .product(bolo).rawMaterial(manteiga).requiredQuantity(80.0).build());

        productRepository.save(bolo);

        // =====================================================================
        // 4. Produto: Biscoito Amanteigado (R$ 8.00)
        //    - 150g Farinha, 100g Açúcar, 60g Manteiga, 30g Ovos
        // =====================================================================
        Product biscoito = Product.builder()
                .code("PRD003")
                .name("Biscoito Amanteigado")
                .price(8.00)
                .build();

        biscoito.getCompositions().add(ProductComposition.builder()
                .product(biscoito).rawMaterial(farinha).requiredQuantity(150.0).build());
        biscoito.getCompositions().add(ProductComposition.builder()
                .product(biscoito).rawMaterial(acucar).requiredQuantity(100.0).build());
        biscoito.getCompositions().add(ProductComposition.builder()
                .product(biscoito).rawMaterial(manteiga).requiredQuantity(60.0).build());
        biscoito.getCompositions().add(ProductComposition.builder()
                .product(biscoito).rawMaterial(ovos).requiredQuantity(30.0).build());

        productRepository.save(biscoito);

        log.info("   ✔ 3 produtos cadastrados com composições.");
        log.info("🚀 Carga de dados iniciais concluída com sucesso!");
    }
}

