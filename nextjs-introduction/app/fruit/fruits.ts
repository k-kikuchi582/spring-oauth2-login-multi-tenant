export const fruits = {
    apple: {
        name: 'apple', emoji: '🍎',
    },
    orange: {
        name: 'orange', emoji: '🍊',
    },
    banana: {
        name: 'banana', emoji: '🍌',
    },
    peach: {
        name: 'peach', emoji: '🍑',
    },
    strawberry: {
        name: 'strawberry', emoji: '🍓',
    },
} as const;

export const fruitTypes = Object.entries(fruits).map(([key, value]) => value.name);
export type Fruit = ArrElement<typeof fruitTypes>

type ArrElement<ArrType> = ArrType extends readonly (infer ElementType)[]
    ? ElementType
    : never;