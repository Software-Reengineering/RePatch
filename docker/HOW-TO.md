# Setting Up the RePatch MySQL Database Using phpMyAdmin
*(For Windows, macOS, and Linux — assuming phpMyAdmin is already running)*

This guide explains how to create the `refactoring_aware_integration` database and import the
provided MySQL dump using **phpMyAdmin**, which already runs inside the docker environment.

> **Which database is this?** This dump holds the **published results** that accompany the paper,
> so you can browse them without running the pipeline. It is *not* the database your own runs write
> to — that one is `refactoring_aware_integration_repatch`, created automatically by RePatch. The
> two coexist; see the main [README](../README.md#results).

---
## 1. Access phpMyAdmin

Open your browser and navigate to ``http://localhost:8080`` — phpMyAdmin is served at the root of
that port (see `phpmyadmin` in `dev-container-repatch/docker-compose.yml`), not under a
`/phpmyadmin` path. Log in with username/password **root/root** and you should see the phpMyAdmin
dashboard.

---

## 2. Create a New Database

### Step 1 — Click **"Databases"** in the top navigation bar.

### Step 2 — Create the database:

- **Database name:** `refactoring_aware_integration`
- **Collation:** `utf8mb4_unicode_ci` (recommended)

Click: **Create**

phpMyAdmin will create an empty database named **refactoring_aware_integration**.

---

## 3. Import the RePatch SQL Dump

### Step 1 — Select the database  
In the left sidebar, click: **refactoring_aware_integration**

You will now be inside the new empty database.

### Step 2 — Click the **"Import"** tab  
Located at the top of the page.

### Step 3 — Upload your SQL file  
Under **"File to import"**:

- Click **Choose File**
- Select your file: `refactoring_aware_integration.sql.zip`. This file is available in the **database-dump** folder at the root of this project.

### Step 4 — Keep all default settings  
Ensure:

- Format: **.SQL.ZIP**
- Partial import: *unchecked*
- Character set: *leave as default (utf-8)*

### Step 5 — Start the import  
Click: **Go**

phpMyAdmin will begin importing the data.  
**This may take several seconds or several minutes depending on file size.**

---

## 4. 🎉 Confirm Successful Import

If the import completes, you will see a green success message:
```
Import has been successfully finished (xxx queries executed)
```

On the left sidebar, expand the **refactoring_aware_integration** database and ensure you see tables such as:

- `project`
- `patch`
- `merge_commit`
- `merge_result`
- `conflicting_file`
- `conflict_block`
- `refactoring`

> These seven are the complete set in this dump. The `refactoring_conflict` table is new in
> RePatch 2.0 and appears only in a live run's database, not here.

### Run a quick test query:

1. Click **SQL**
2. Paste:
    ```sql
    SELECT COUNT(*) FROM merge_commit;
    ```
3. Click **Go**. If you see a number (e.g., 300), the database loaded correctly.


## Example Use Cases

1. Show me all merge results (Git & RePatch) that correspond to *PR 12660*:
   ```sql
    SELECT mr.*
    FROM merge_result mr
    JOIN patch p ON mr.patch_id = p.id
    WHERE p.number = 12660;
    ```

2. Retrieve all conflicting files associated with the merge results of patch *PR 12660*.
   ```sql
    SELECT
    cf.*
    FROM conflicting_file cf
    JOIN merge_result mr
        ON cf.merge_result_id = mr.id
    JOIN patch p
        ON mr.patch_id = p.id
    WHERE p.number = 12660;
    ```

3. Returns all conflict blocks belonging to conflicting files for the merge results of *PR 12660*.
    ```sql
    SELECT cb.*
    FROM conflict_block cb
    JOIN conflicting_file cf
        ON cb.conflicting_file_id = cf.id
    JOIN merge_result mr
        ON cf.merge_result_id = mr.id
    JOIN patch p
        ON mr.patch_id = p.id
    WHERE p.number = 12660;
    ```

4. Retrieve all refactoring instances detected for the merge commit associated with *PR 12660*.
    ```sql
    SELECT r.*
    FROM refactoring r
    JOIN merge_commit mc
        ON r.merge_commit_id = mc.id
    JOIN patch p
        ON mc.patch_id = p.id
    WHERE p.number = 12660;
    ```