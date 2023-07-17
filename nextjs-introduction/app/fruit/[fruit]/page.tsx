import {Fruit, fruits, fruitTypes} from "@/app/fruit/fruits";

const sleep = (ms: number) => new Promise((resolve) => setTimeout(resolve, ms));

export async function generateStaticParams() {
    await sleep(3000);
    return fruitTypes;
}

export default function Fruit({
    params,
                              }: {
    params: { fruit: Fruit, },
}) {

    return (
        <div>
            This is a {params.fruit} {fruits[params.fruit].emoji}
        </div>
    )
}