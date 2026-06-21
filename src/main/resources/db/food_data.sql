SHOW VARIABLES LIKE 'local_infile';
SET GLOBAL local_infile = 1;

show global variables like 'local_infile';
set global local_infile = true;

show global variables like 'local_infile';
show session variables like 'local_infile';


LOAD DATA LOCAL INFILE 'C:/csv/food_import.csv'
    IGNORE
    INTO TABLE food
    CHARACTER SET utf8mb4
    FIELDS TERMINATED BY ','
    OPTIONALLY ENCLOSED BY '"'
    ESCAPED BY '"'
    LINES TERMINATED BY '\r\n'
    IGNORE 1 LINES
    (
     @no_value,
     @food_code,
     @food_name,
     @doc_nm,
     @trans_txt,
     @org_food_url,
     @material,
     @recipe,
     @doc_cd,
     @author,
     @year_value,
     @category,
     @feature
        )
    SET
        food_id = TRIM(@food_code),
        category = NULLIF(TRIM(@category), ''),
        food_name = NULLIF(TRIM(@food_name), ''),
        food_feature = NULLIF(TRIM(@feature), ''),
        ingredients = NULLIF(@material, ''),
        recipe = NULLIF(@recipe, ''),
        doc_nm = NULLIF(TRIM(@doc_nm), ''),
        author = NULLIF(TRIM(@author), ''),
        published_year = NULLIF(TRIM(@year_value), ''),
        trans_txt = NULLIF(@trans_txt, ''),
        org_food_url = NULLIF(TRIM(@org_food_url), '');