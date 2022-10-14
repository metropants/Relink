import type {NextPage} from 'next'
import {
    Container,
    Group,
    TextInput,
    Button,
    Checkbox,
    Title,
    Tooltip,
    Table,
    Anchor, Divider,
} from "@mantine/core";
import {useForm} from "@mantine/form";
import api from "../api";
import {useEffect, useState} from "react";
import Cookies from "js-cookie"
import {useRouter} from "next/router";

const expression = /(https?:\/\/(?:www\.|(?!www))[a-zA-Z0-9][a-zA-Z0-9-]+[a-zA-Z0-9]\.[^\s]{2,}|www\.[a-zA-Z0-9][a-zA-Z0-9-]+[a-zA-Z0-9]\.[^\s]{2,}|https?:\/\/(?:www\.|(?!www))[a-zA-Z0-9]+\.[^\s]{2,}|www\.[a-zA-Z0-9]+\.[^\s]{2,})/gi;
const regex = new RegExp(expression);

interface RedirectData {
    id: string;
    original: string;
    shortened: string;
    expires?: boolean;
}

const Home: NextPage = () => {
    const router = useRouter();
    const [redirects, setRedirects] = useState<RedirectData[] | null>([]);
    useEffect(() => {
        async function loadRedirectFromCookies() {
            const strings = Cookies.get();
            const redirectArray = [];
            for (const data in strings) {
                const cookie = Cookies.get(data);
                if (cookie) {
                    const parsed = JSON.parse(cookie);
                    redirectArray.push(parsed);
                }
            }
            return redirectArray;
        }

        loadRedirectFromCookies().then(result => setRedirects(result));
    }, [redirects]);

    const form = useForm({
        initialValues: {
            url: "",
            expires: true,
        },

        validate: {
            url: (value) => (value.length > 0 && value.match(regex)) ? null : true,
        }
    });

    async function createRedirect(values: { url: string, expires: boolean }) {
        const response = await api.post("/", {link: values.url, expires: values.expires});
        if (response) {
            const data: RedirectData = response.data;
            Cookies.set(`redirect_${data.id}`, JSON.stringify(data));
            redirects?.push(data);
            form.reset();
        }
    }

    const rows = redirects?.map((redirect) => (
        <tr key={redirect.id}>
            <td>{redirect.id}</td>
            <td>
                <Anchor<'a'> size="sm" onClick={(event) => {
                    event.preventDefault();
                    router.push(redirect.original).catch(console.error);
                }}>
                    {redirect.original}
                </Anchor>
            </td>
            <td>
                <Anchor<'a'> size="sm" onClick={(event) => {
                    event.preventDefault();
                    router.push(redirect.original).catch(console.error);
                }}>
                    {redirect.shortened}
                </Anchor>
            </td>
            <td>{redirect.expires}</td>
        </tr>
    ));

    return (
        <Container>
            <Title mb="xl" order={2}>URL Shortener</Title>
            <form onSubmit={form.onSubmit(values => createRedirect(values))}>
                <Group>
                    <TextInput placeholder="Shorten your link" style={{flex: 1}}
                               error={form.errors.url} {...form.getInputProps("url")}/>
                    <Button type="submit" sx={{transform: "none !important"}}>Shorten</Button>
                </Group>
                <Tooltip position="bottom-start" withArrow multiline width={210}
                         label="If checked the redirect url will be removed after 24 hours.">
                    <Checkbox mt="sm" label="Expires" {...form.getInputProps("expires", {type: "checkbox"})}
                              styles={{root: {fontWeight: 500}}}/>
                </Tooltip>
            </form>
            <Divider mt={64} mb={64}/>
            <Title order={2}>History</Title>
            <Table mt="lg" striped fontSize="xs" horizontalSpacing={0}>
                <thead>
                <tr>
                    <th>ID</th>
                    <th>Original</th>
                    <th>Shortened</th>
                    <th>Expires</th>
                </tr>
                </thead>
                <tbody>{rows}</tbody>
            </Table>
        </Container>
    )
}

export default Home
