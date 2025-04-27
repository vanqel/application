alter table cars
    add search text generated always as (
        mark || ' ' || model || ' ' || year_start || ' ' || mark_ru || ' ' || model_ru || ' ' || year_start
        ) stored;

create index i_cars on cars(search)
