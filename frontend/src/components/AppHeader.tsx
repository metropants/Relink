import {createStyles, Header, Container, Text, Group, Badge} from '@mantine/core';
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {faLink} from "@fortawesome/free-solid-svg-icons";
import {useRouter} from "next/router";

const useStyles = createStyles(() => ({
    header: {
        display: 'flex',
        justifyContent: 'space-between',
        alignItems: 'center',
        height: '100%',
    },
    branding: {
        userSelect: "none",
        ":hover": {
            cursor: "pointer",
        }
    }
}));

function AppHeader() {
    const {classes} = useStyles();
    const router = useRouter();

    return (
        <Header height={44} mb={120}>
            <Container className={classes.header}>
                <Group className={classes.branding} spacing="xs" onClick={() => router.push("/")}>
                    <FontAwesomeIcon icon={faLink}/>
                    <Text weight={500}>Relink</Text>
                    <Badge variant="filled" size="xs" radius="xs" mb="md">1.0.0</Badge>
                </Group>
            </Container>
        </Header>
    );
}

export default AppHeader;
