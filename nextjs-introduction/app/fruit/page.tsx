import {fruits, fruitTypes} from "@/app/fruit/fruits";
import Link from "next/link";

export default function Fruits() {
    return (
        <>
            <div>フルーツ好きですか？</div>
            <table>
                <thead>
                <tr>
                    <th>Name</th>
                    <th>Emoji</th>
                </tr>
                </thead>
                <tbody>
                {fruitTypes
                    .map(fruitType => fruits[fruitType])
                    .map(fruit => (
                        <tr key={fruit.name}>
                            <td>
                                <Link href={`/fruit/${fruit.name}`} className='text-blue-600 hover:text-blue-900 underline'>
                                    {fruit.name}
                                </Link>
                            </td> <td>{fruit.emoji}</td>

                        </tr>
                    ))
                }
                </tbody>
            </table>
        </>
    );
}